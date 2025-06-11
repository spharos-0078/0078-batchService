package com.pieceofcake.batch_service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@RequiredArgsConstructor
@Component
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailyFragmentAggregationJob;
    private final Job monthlyFragmentAggregationJob;
    private final Job yearlyFragmentAggregationJob;

    @Scheduled(cron = "0 0 23 * * * ")
    public void runDailyFragmentAggregationJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "DailyFragmentAggregationJob")
                .addString("date", LocalDate.now().toString())
                .toJobParameters();
        jobLauncher.run(dailyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 0 23 1 * * ")
    public void runMonthlyFragmentAggregationJob() throws Exception{
        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(now).minusMonths(1);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "MonthlyFragmentAggregationJob")
                .addString("startDate", previousMonth.atDay(1).toString())
                .addString("endDate", previousMonth.atEndOfMonth().toString())
                .toJobParameters();
        jobLauncher.run(monthlyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 22 14 * * * ")
    public void runYearlyFragmentAggregationJob() throws Exception{
        int year = LocalDate.now().minusMonths(1).getYear();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "YearlyFragmentAggregationJob")
                .addLong("year", (long) year)
                .toJobParameters();
        jobLauncher.run(yearlyFragmentAggregationJob, jobParameters);
    }

}