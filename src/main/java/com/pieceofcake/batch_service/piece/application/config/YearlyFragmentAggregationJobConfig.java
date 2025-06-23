package com.pieceofcake.batch_service.piece.application.config;

import com.pieceofcake.batch_service.piece.dto.in.MonthlyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.dto.in.YearlyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.entity.MonthlyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.entity.YearlyFragmentPriceAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.YearlyAggregationRepository;
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
public class YearlyFragmentAggregationJobConfig {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final YearlyAggregationRepository yearlyRepository;

    @Bean
    public Job yearlyFragmentAggregationJob() {
        return new JobBuilder("yearlyFragmentAggregationJob", jobRepository)
                .start(yearlyAggregaionStep())
                .build();
    }

    @Bean
    public Step yearlyAggregaionStep() {
        return new StepBuilder("yearlyAggregationStep", jobRepository)
                .<YearlyFragmentAggregationDto, YearlyFragmentPriceAggregation>chunk(500, transactionManager)
                .reader(yearlyAggregationReader(0))
                .processor(yearlyAggregationProcessor())
                .writer(yearlyAggregateWriter())
                .build();
    }


    @Bean
    @StepScope
    public JdbcCursorItemReader<YearlyFragmentAggregationDto> yearlyAggregationReader(
            @Value("#{jobParameters['year']}") int year
    ) {
        JdbcCursorItemReader<YearlyFragmentAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(
                "SELECT " +
                        "  piece_product_uuid, " +
                        "  (SELECT starting_price " +
                        "   FROM monthly_fragment_price_aggregation h2 " +
                        "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND YEAR(start_date) = ? " +
                        "   ORDER BY id ASC LIMIT 1) AS starting_price, " +
                        "  (SELECT closing_price " +
                        "   FROM monthly_fragment_price_aggregation h2 " +
                        "   WHERE h2.piece_product_uuid = h1.piece_product_uuid AND YEAR(start_date) = ? " +
                        "   ORDER BY id DESC LIMIT 1) AS closing_price, " +
                        "  MIN(minimum_price) AS minimum_price, " +
                        "  MAX(maximum_price) AS maximum_price, " +
                        "  TRUNCATE(AVG(average_price), 2) AS average_price, " +
                        "  SUM(trade_quantity) as trade_quantity, " +
                        "  YEAR(start_date) as year " +
                        "FROM monthly_fragment_price_aggregation h1 " +
                        "WHERE YEAR(start_date) = ? " +
                        "GROUP BY piece_product_uuid, YEAR(start_date)"
        );
        reader.setPreparedStatementSetter(ps -> {
            ps.setInt(1, year);
            ps.setInt(2, year);
            ps.setInt(3, year);

        });
        reader.setRowMapper((rs, rowNum) -> YearlyFragmentAggregationDto.builder()
                .pieceProductUuid(rs.getString("piece_product_uuid"))
                .startingPrice(rs.getLong("starting_price"))
                .closingPrice(rs.getLong("closing_price"))
                .minimumPrice(rs.getLong("minimum_price"))
                .maximumPrice(rs.getLong("maximum_price"))
                .averagePrice(rs.getLong("average_price"))
                .tradeQuantity(rs.getLong("trade_quantity"))
                .year(rs.getInt("year"))
                .build()
        );
        reader.setName("yearlyAggregationReader");
        return reader;
    }

    @Bean
    public ItemProcessor<YearlyFragmentAggregationDto, YearlyFragmentPriceAggregation> yearlyAggregationProcessor() {
        return YearlyFragmentAggregationDto::toEntity;
    }

    @Bean
    public ItemWriter<YearlyFragmentPriceAggregation> yearlyAggregateWriter(){
        return yearlyRepository::saveAll;
    }

}
