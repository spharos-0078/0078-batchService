package com.pieceofcake.batch_service.piece.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.batch_service.chart.dto.out.GetChartRealTimeResponseDto;
import com.pieceofcake.batch_service.piece.entity.HourlyFragmentHistory;
import com.pieceofcake.batch_service.piece.infrastructure.HourlyFragmentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HourlyFragmentHistoryJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final HourlyFragmentHistoryRepository hourlyFragmentHistoryRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Job hourlyFragmentHistoryJob() {
        return new JobBuilder("hourlyFragmentHistoryJob", jobRepository)
                .start(hourlyFragmentHistoryStep())
                .build();
    }

    @Bean
    public Step hourlyFragmentHistoryStep() {
        return new StepBuilder("hourlyFragmentHistoryStep", jobRepository)
                .<GetChartRealTimeResponseDto, HourlyFragmentHistory>chunk(100, transactionManager)
                .reader(hourlyFragmentHistoryReader(null))
                .processor(hourlyFragmentHistoryProcessor())
                .writer(hourlyFragmentHistoryWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<GetChartRealTimeResponseDto> hourlyFragmentHistoryReader(
            @Value("#{jobParameters['date']}") String dateStr
    ) {
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        List<GetChartRealTimeResponseDto> items = new ArrayList<>();

        try {
            // Redis에서 모든 pieceVolume 키 패턴을 찾아서 데이터를 수집
            String pattern = "pieceVolume:*:" + date;
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    // Redis 리스트에서 모든 데이터를 가져옴
                    List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
                    
                    if (jsonList != null) {
                        for (String json : jsonList) {
                            try {
                                GetChartRealTimeResponseDto dto = objectMapper.readValue(json, GetChartRealTimeResponseDto.class);
                                items.add(dto);
                            } catch (Exception e) {
                                log.error("Failed to parse JSON: {}", json, e);
                            }
                        }
                    }
                }
            }
            
            log.info("Found {} items from Redis for date: {}", items.size(), date);
            
        } catch (Exception e) {
            log.error("Error reading from Redis", e);
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<GetChartRealTimeResponseDto, HourlyFragmentHistory> hourlyFragmentHistoryProcessor() {
        return item -> {
            return HourlyFragmentHistory.builder()
                    .pieceProductUuid(item.getPieceProductUuid())
                    .price(item.getPiecePrice())
                    .quantity(item.getMatchedQuantity())
                    .date(item.getMatchedTime().toLocalDate())
                    .build();
        };
    }

    @Bean
    public ItemWriter<HourlyFragmentHistory> hourlyFragmentHistoryWriter() {
        return hourlyFragmentHistoryRepository::saveAll;
    }
}
