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
import java.time.LocalTime;
import java.time.YearMonth;

@RequiredArgsConstructor
@Component
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailyFragmentAggregationJob;
    private final Job monthlyFragmentAggregationJob;
    private final Job yearlyFragmentAggregationJob;
    private final Job minutelyFragmentAggregationJob;
    private final Job dailyMemberAssetAggregationJob;
    private final Job bestPieceProductAggregationJob;

    @Scheduled(cron = "0 */1 * * * *")
    public void runMinutelyFragmentAggregationJob() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.withSecond(0).withNano(0);
        LocalDateTime endTime = now.withSecond(59).withNano(0);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobName", "minutelyFragmentAggregationJob")
                .addString("time", now.toString())
                .addString("startTime", startTime.toString()) // ex: 22:00:00
                .addString("endTime", endTime.toString())     // ex: 22:01:00
                .toJobParameters();

        jobLauncher.run(minutelyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 5 22 * * * ")
    public void runBestPieceProductAggregationJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "BestPieceProductAggregationJob")
                .addString("date", LocalDate.now().toString())
                .toJobParameters();
        jobLauncher.run(bestPieceProductAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 0 22 * * * ")
    public void runDailyFragmentAggregationJob() throws Exception{
        LocalDate date = LocalDate.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "DailyFragmentAggregationJob")
                .addString("date", date.toString())
                .toJobParameters();
        jobLauncher.run(dailyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 20 22 L * * ")
    public void runMonthlyFragmentAggregationJob() throws Exception{
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "MonthlyFragmentAggregationJob")
                .addString("startDate", startDate.toString())
                .addString("endDate", endDate.toString())
                .toJobParameters();
        jobLauncher.run(monthlyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 30 22 L 12 *")
    public void runYearlyFragmentAggregationJob() throws Exception {
        int year = LocalDate.now().getYear();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "YearlyFragmentAggregationJob")
                .addLong("year", (long) year)
                .toJobParameters();
        jobLauncher.run(yearlyFragmentAggregationJob, jobParameters);
    }

    @Scheduled(cron = "0 0/10 * * * * ")
    public void runDailyMemberAssetAggregationJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "DailyMemberAssetAggregationJob")
                .toJobParameters();
        jobLauncher.run(dailyMemberAssetAggregationJob, jobParameters);
    }
}