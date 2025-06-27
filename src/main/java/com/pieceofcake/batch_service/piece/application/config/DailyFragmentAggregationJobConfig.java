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
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        LocalDate date = LocalDate.parse(dateStr);

        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        JdbcCursorItemReader<DailyFragmentAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT " +
                "  piece_product_uuid, " +
                "  (SELECT price " +
                "   FROM hourly_fragment_history h2 " +
                "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND h2.date = ? " +
                "   ORDER BY id ASC LIMIT 1) AS starting_price, " +
                "  (SELECT price " +
                "   FROM hourly_fragment_history h2 " +
                "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND h2.date = ? " +
                "   ORDER BY id DESC LIMIT 1) AS closing_price, " +
                "  MIN(price) AS minimum_price, " +
                "  MAX(price) AS maximum_price, " +
                "  TRUNCATE(AVG(price),2) AS average_price, " +
                "  SUM(quantity) as trade_quantity, " +
                "  date " +
                "FROM hourly_fragment_history h1 " +
                "WHERE date = ? " +
                "GROUP BY piece_product_uuid");

        reader.setPreparedStatementSetter(ps -> {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setDate(3, java.sql.Date.valueOf(date));
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
                    .date(rs.getDate("date").toLocalDate())
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