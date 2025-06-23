package com.pieceofcake.batch_service.piece.application.config;

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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Configuration
@RequiredArgsConstructor
public class DailyFragmentAggregationJobConfig {

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final DailyAggregationRepository dailyAggregationRepository;

    // Job에서 Step을 파라미터로 받아서 사용
    @Bean
    public Job dailyFragmentAggregationJob() {
        return new JobBuilder("dailyFragmentAggregationJob", jobRepository)
                .start(dailyAggregationStep())
                .build();
    }

    // Step은 @StepScope로 jobParameters 주입 받음
    @Bean
    public Step dailyAggregationStep() {
        return new StepBuilder("dailyAggregationStep", jobRepository)
                .<DailyFragmentAggregationDto, DailyFragmentPriceAggregation>chunk(500, transactionManager)
                .reader(dailyAggregationReader(null))
                .processor(dailyAggregationProcessor())
                .writer(dailyAggregateWriter())
                .build();
    }

    // Reader도 @StepScope로 jobParameters 주입
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
                "  COUNT(*) as trade_quantity, " +
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
                    .startingPrice(rs.getDouble("starting_price"))
                    .closingPrice(rs.getDouble("closing_price"))
                    .minimumPrice(rs.getDouble("minimum_price"))
                    .maximumPrice(rs.getDouble("maximum_price"))
                    .averagePrice(rs.getDouble("average_price"))
                    .tradeQuantity(rs.getLong("trade_quantity"))
                    .date(rs.getDate("date").toLocalDate())
                    .build();
        });
        return reader;
    }

    @Bean
    public ItemProcessor<DailyFragmentAggregationDto, DailyFragmentPriceAggregation> dailyAggregationProcessor() {
        //이벤트 발행
        return DailyFragmentAggregationDto::toEntity;
    }

    @Bean
    public ItemWriter<DailyFragmentPriceAggregation> dailyAggregateWriter(){
        return dailyAggregationRepository::saveAll;
    }
}
