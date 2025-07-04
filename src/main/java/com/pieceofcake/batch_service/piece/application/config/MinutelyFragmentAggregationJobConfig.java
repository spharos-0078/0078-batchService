package com.pieceofcake.batch_service.piece.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.batch_service.piece.dto.in.MinutelyFragmentAggregationDto;
import com.pieceofcake.batch_service.piece.entity.MinutelyFragmentAggregation;
import com.pieceofcake.batch_service.piece.infrastructure.MinutelyFragmentAggregationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinutelyFragmentAggregationJobConfig {


    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MinutelyFragmentAggregationRepository minutelyRepository;

    @Bean
    public Job minutelyFragmentAggregationJob() {
        return new JobBuilder("minutelyFragmentAggregationJob", jobRepository)
                .start(minutelyFragmentAggregationStep())
                .build();
    }

    @Bean
    public Step minutelyFragmentAggregationStep() {
        return new StepBuilder("minutelyFragmentAggregationStep", jobRepository)
                .<MinutelyFragmentAggregationDto, MinutelyFragmentAggregation>chunk(100, transactionManager)
                .reader(minutelyFragmentAggregationReader(null,null))
                .processor(minutelyFragmentAggregationProcessor())
                .writer(minutelyFragmentAggregationWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<MinutelyFragmentAggregationDto> minutelyFragmentAggregationReader(
            @Value("#{jobParameters['startTime']}") String startTime,
            @Value("#{jobParameters['endTime']}") String endTime
    ) {
        JdbcCursorItemReader<MinutelyFragmentAggregationDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("""
        SELECT
          piece_product_uuid,
          SUM(matched_quantity) AS quantity,
          MIN(piece_price) AS minimum_price,
          MAX(piece_price) AS maximum_price,
          AVG(piece_price) AS average_price,
          SUBSTRING_INDEX(GROUP_CONCAT(piece_price ORDER BY matched_time ASC), ',', 1) AS starting_price,
          SUBSTRING_INDEX(GROUP_CONCAT(piece_price ORDER BY matched_time DESC), ',', 1) AS closing_price,
          DATE_FORMAT(MIN(matched_time), '%Y-%m-%d %H:%i:00') AS date
        FROM piece_matched_history
        WHERE matched_time >= ?
          AND matched_time <  ?
        GROUP BY piece_product_uuid
    """);

        reader.setPreparedStatementSetter(ps -> {
            ps.setString(1, startTime);
            ps.setString(2, endTime);
        });

        reader.setRowMapper((rs, rowNum) -> MinutelyFragmentAggregationDto.builder()
                .pieceProductUuid(rs.getString("piece_product_uuid"))
                .startingPrice(rs.getLong("starting_price"))
                .closingPrice(rs.getLong("closing_price"))
                .minimumPrice(rs.getLong("minimum_price"))
                .maximumPrice(rs.getLong("maximum_price"))
                .averagePrice(rs.getLong("average_price"))
                .quantity(rs.getInt("quantity"))
                .date(rs.getTimestamp("date").toLocalDateTime())
                .build());

        return reader;
    }

    @Bean
    public ItemProcessor<MinutelyFragmentAggregationDto, MinutelyFragmentAggregation> minutelyFragmentAggregationProcessor(){
        return MinutelyFragmentAggregationDto::toEntity;
    }

    @Bean
    public ItemWriter<MinutelyFragmentAggregation> minutelyFragmentAggregationWriter(){
        return items->{
            for (MinutelyFragmentAggregation item : items) {
                redisTemplate.convertAndSend("fragment-price-topic", item);
            }
            minutelyRepository.saveAll(items);
        };
    }
}
