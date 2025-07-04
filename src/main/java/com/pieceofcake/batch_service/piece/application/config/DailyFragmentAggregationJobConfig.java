package com.pieceofcake.batch_service.piece.application.config;

import com.pieceofcake.batch_service.kafka.producer.DailyTradePieceEvent;
import com.pieceofcake.batch_service.kafka.producer.KafkaProducer;
import com.pieceofcake.batch_service.piece.dto.in.DailyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.entity.DailyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.DailyAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DailyFragmentAggregationJobConfig {

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final DailyAggregationRepository dailyAggregationRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisTemplate<String, Long> redisTemplate;

    @Bean
    public Job dailyFragmentAggregationJob() {
        return new JobBuilder("dailyFragmentAggregationJob", jobRepository)
                .start(dailyAggregationStep())
                .build();
    }

    @Bean
    public Step dailyAggregationStep() {
        return new StepBuilder("dailyAggregationStep", jobRepository)
                .<DailyFragmentAggregationDto, DailyFragmentPriceAggregation>chunk(500, transactionManager)
                .reader(dailyAggregationReader(null))
                .processor(dailyAggregationProcessor())
                .writer(dailyAggregateWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<DailyFragmentAggregationDto> dailyAggregationReader(
            @Value("#{jobParameters['date']}") String dateStr
    ) {
        JdbcCursorItemReader<DailyFragmentAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("""
        SELECT
            piece_product_uuid,
            (SELECT starting_price
             FROM minutely_fragment_aggregation m2
             WHERE m2.piece_product_uuid = m1.piece_product_uuid
               AND DATE(m2.date) = ?
             ORDER BY m2.date ASC
             LIMIT 1) AS starting_price,
            (SELECT closing_price
             FROM minutely_fragment_aggregation m3
             WHERE m3.piece_product_uuid = m1.piece_product_uuid
               AND DATE(m3.date) = ?
             ORDER BY m3.date DESC
             LIMIT 1) AS closing_price,
            MIN(minimum_price) AS minimum_price,
            MAX(maximum_price) AS maximum_price,
            AVG(average_price) AS average_price,
            SUM(quantity) AS trade_quantity
        FROM minutely_fragment_aggregation m1
        WHERE DATE(m1.date) = ?
        GROUP BY piece_product_uuid
        """);

        reader.setPreparedStatementSetter(ps -> {
            ps.setDate(1, java.sql.Date.valueOf(dateStr));
            ps.setDate(2, java.sql.Date.valueOf(dateStr));
            ps.setDate(3, java.sql.Date.valueOf(dateStr));
        });

        reader.setRowMapper((rs, rowNum) -> {

            return DailyFragmentAggregationDto.builder()
                    .pieceProductUuid(rs.getString("piece_product_uuid"))
                    .startingPrice(rs.getLong("starting_price"))
                    .closingPrice(rs.getLong("closing_price"))
                    .minimumPrice(rs.getLong("minimum_price"))
                    .maximumPrice(rs.getLong("maximum_price"))
                    .averagePrice(rs.getLong("average_price"))
                    .tradeQuantity(rs.getLong("trade_quantity"))
                    .date(LocalDate.parse(dateStr))
                    .build();
        });
        return reader;
    }

    @Bean
    public ItemProcessor<DailyFragmentAggregationDto, DailyFragmentPriceAggregation> dailyAggregationProcessor() {

        return item -> {
            DailyFragmentPriceAggregation entity = item.toEntity();

            //종료가 레디스 저장
            String key = "piece:" + entity.getPieceProductUuid() + ":closingprice";
            redisTemplate.opsForValue().set(key , entity.getClosingPrice());

            return entity;
        };
    }

    @Bean
    public ItemWriter<DailyFragmentPriceAggregation> dailyAggregateWriter(){
        return items -> {
            // 데이터베이스에 저장
            dailyAggregationRepository.saveAll(items);
            System.out.println("배치 실행");
            // 상품별로 한 번만 이벤트 발행
            items.forEach(entity -> {
                DailyTradePieceEvent event = DailyTradePieceEvent.builder()
                    .pieceProductUuid(entity.getPieceProductUuid())
                    .closingPrice(entity.getClosingPrice())
                    .tradeQuantity(entity.getTradeQuantity())
                    .build();
                
                try {
                    kafkaProducer.sendPieceReadEvent(event);
                } catch (Exception e) {
                    // Kafka 연결 실패 시에도 배치 작업은 계속 진행
                    System.err.println("Kafka 이벤트 발행 실패: " + e.getMessage());
                }
            });
        };
    }
}