package com.pieceofcake.batch_service.piece.application.config;

import com.pieceofcake.batch_service.piece.dto.in.BestPieceProductAggregationDto;
import com.pieceofcake.batch_service.piece.dto.in.DailyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.entity.BestPieceProductAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.BestPieceProductAggregationRepository;
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
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BestPieceProductAggregationJobConfig {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final BestPieceProductAggregationRepository bestRepository;

    @Bean
    public Job bestPieceProductAggregationJob() {
        return new JobBuilder("bestPieceProductAggregationJob",jobRepository)
                .start(bestPieceProductAggregationStep())
                .build();
    }

    @Bean
    public Step bestPieceProductAggregationStep() {
        return new StepBuilder("bestPieceProductAggregationStep",jobRepository)
                .<BestPieceProductAggregationDto, BestPieceProductAggregation>chunk(500, transactionManager)
                .reader(bestAggregationReader(null))
                .processor(bestAggregationProcessor())
                .writer(bestAggregationWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<BestPieceProductAggregationDto> bestAggregationReader(
            @Value("#{jobParameters['date']}") String dateStr
    ) {
        LocalDate date = LocalDate.parse(dateStr);

        Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        JdbcCursorItemReader<BestPieceProductAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * " +
                "from ( " +
                "    select " +
                "        h.piece_product_uuid, " +
                "        sum(h.quantity) as total_quantity, " +
                "        rank() over (order by sum(h.quantity) desc) as ranking " +
                "    from hourly_fragment_history h " +
                "    where h.date = ? " +
                "    group by h.piece_product_uuid " +
                ") ranked " +
                "limit 50");

        reader.setPreparedStatementSetter(ps -> {
            ps.setDate(1, java.sql.Date.valueOf(date));
        });

        reader.setRowMapper((rs, rowNum) -> {
            return BestPieceProductAggregationDto.builder()
                    .pieceProductUuid(rs.getString("piece_product_uuid"))
                    .ranking(rs.getInt("ranking"))
                    .date(date)
                    .build();
        });
        return reader;
    }

    @Bean
    public ItemProcessor<BestPieceProductAggregationDto,BestPieceProductAggregation> bestAggregationProcessor() {
        return BestPieceProductAggregationDto::toEntity;
    }

    @Bean
    public ItemWriter<BestPieceProductAggregation> bestAggregationWriter() {
        return bestRepository::saveAll;
    }
}
