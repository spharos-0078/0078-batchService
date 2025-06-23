package com.pieceofcake.batch_service.piece.application.config;

import com.pieceofcake.batch_service.piece.dto.in.MonthlyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.MonthlyAggregationRepository;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@RequiredArgsConstructor
@Configuration
public class MonthlyFragmentAggregationJobConfig {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MonthlyAggregationRepository monthlyRepository;

    @Bean
    public Job monthlyFragmentAggregationJob() {
        return new JobBuilder("monthlyFragmentAggregationJob", jobRepository)
                .start(monthlyAggregaionStep())
                .build();
    }

    @Bean
    public Step monthlyAggregaionStep() {
        return new StepBuilder("monthlyAggregationStep", jobRepository)
                .<MonthlyFragmentAggregationDto, MonthlyFragmentPriceAggregation>chunk(500, transactionManager)
                .reader(monthlyAggregationReader(null,null))
                .processor(monthlyAggregationProcessor())
                .writer(monthlyAggregateWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<MonthlyFragmentAggregationDto> monthlyAggregationReader(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate
    ) {
        JdbcCursorItemReader<MonthlyFragmentAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(
                "SELECT " +
                        "  piece_product_uuid, " +
                        "  (SELECT starting_price " +
                        "   FROM daily_fragment_price_aggregation h2 " +
                        "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND date between ? and ? " +
                        "   ORDER BY id ASC LIMIT 1) AS starting_price, " +
                        "  (SELECT closing_price " +
                        "   FROM daily_fragment_price_aggregation h2 " +
                        "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND date between ? and ? " +
                        "   ORDER BY id DESC LIMIT 1) AS closing_price, " +
                        "  MIN(minimum_price) AS minimum_price, " +
                        "  MAX(maximum_price) AS maximum_price, " +
                        "  TRUNCATE(AVG(average_price), 2) AS average_price, " +
                        "  SUM(trade_quantity) as trade_quantity " +
                        "FROM daily_fragment_price_aggregation h1 " +
                        "WHERE date between ? and ? " +
                        "GROUP BY piece_product_uuid"
        );
        reader.setPreparedStatementSetter(ps -> {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ps.setDate(3, java.sql.Date.valueOf(startDate));
            ps.setDate(4, java.sql.Date.valueOf(endDate));
            ps.setDate(5, java.sql.Date.valueOf(startDate));
            ps.setDate(6, java.sql.Date.valueOf(endDate));

        });
        reader.setRowMapper((rs, rowNum) -> MonthlyFragmentAggregationDto.builder()
                .pieceProductUuid(rs.getString("piece_product_uuid"))
                .startingPrice(rs.getLong("starting_price"))
                .closingPrice(rs.getLong("closing_price"))
                .minimumPrice(rs.getLong("minimum_price"))
                .maximumPrice(rs.getLong("maximum_price"))
                .averagePrice(rs.getLong("average_price"))
                .tradeQuantity(rs.getLong("trade_quantity"))
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .build()
        );
        reader.setName("monthlyAggregationReader");
        return reader;
    }

    @Bean
    public ItemProcessor<MonthlyFragmentAggregationDto, MonthlyFragmentPriceAggregation> monthlyAggregationProcessor() {
        return MonthlyFragmentAggregationDto::toEntity;
    }

    @Bean
    public ItemWriter<MonthlyFragmentPriceAggregation> monthlyAggregateWriter(){
        return monthlyRepository::saveAll;
    }
}
