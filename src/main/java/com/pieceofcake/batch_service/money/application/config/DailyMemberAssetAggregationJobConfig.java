package com.pieceofcake.batch_service.money.application.config;

import com.pieceofcake.batch_service.money.entity.DailyMemberAssetAggregation;
import com.pieceofcake.batch_service.money.entity.ViewDailyMemberAssetAggregation;
import com.pieceofcake.batch_service.money.infrastructure.DailyMemberAssetAggregationRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Configuration
public class DailyMemberAssetAggregationJobConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final DailyMemberAssetAggregationRepository repository;

    @Bean
    public Job dailyMemberAssetAggregationJob() {
        return new JobBuilder("dailyMemberAssetAggregationJob", jobRepository)
                .start(clearDailyMemberAssetAggregationStep())
                .next(dailyMemberAssetAggregationStep())
                .build();
    }

    @Bean
    public Step clearDailyMemberAssetAggregationStep() {
        return new StepBuilder("clearDailyMemberAssetAggregationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    repository.deleteAllInBatch();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step dailyMemberAssetAggregationStep() {
        return new StepBuilder("DailyMemberAssetAggregationStep", jobRepository)
                .<ViewDailyMemberAssetAggregation,DailyMemberAssetAggregation>chunk(500, transactionManager)
                .reader(dailyMemberAssetAggregationItemReader())
                .processor(dailyMemberAssetAggregationItemProcessor())
                .writer(dailyMemberAssetAggregationItemWriter())
                .build();
    }

    @Bean
    public JpaCursorItemReader<ViewDailyMemberAssetAggregation> dailyMemberAssetAggregationItemReader() {
        return new JpaCursorItemReaderBuilder<ViewDailyMemberAssetAggregation>()
                .name("DailyMemberAssetAggregationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM ViewDailyMemberAssetAggregation v")
                .build();
    }

    @Bean
    public ItemProcessor<ViewDailyMemberAssetAggregation, DailyMemberAssetAggregation> dailyMemberAssetAggregationItemProcessor() {
        return source -> {
            return DailyMemberAssetAggregation.builder()
                    .memberUuid(source.getMemberUuid())
                    .totalFunding(source.getTotalFunding())
                    .totalPiece(source.getTotalPiece())
                    .totalMoney(source.getTotalMoney())
                    .totalBid(source.getTotalBid())
                    .date(LocalDateTime.now())
                    .build();
        };
    }

    @Bean
    public ItemWriter<DailyMemberAssetAggregation> dailyMemberAssetAggregationItemWriter() {
        return repository::saveAll;
    }

}
