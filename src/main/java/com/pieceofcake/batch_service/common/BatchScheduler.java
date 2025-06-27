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
    private final Job hourlyFragmentHistoryJob;
//    private final Job dailyMemberAssetAggregationJob;

    @Scheduled(cron = "0 25 2 * * * ")
    public void runHourlyFragmentHistoryJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "HourlyFragmentHistoryJob")
                .addString("date", LocalDate.now().toString())
                .toJobParameters();
        jobLauncher.run(hourlyFragmentHistoryJob, jobParameters);
    }

    @Scheduled(cron = "0 26 2 * * * ")
    public void runDailyFragmentAggregationJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "DailyFragmentAggregationJob")
                .addString("date", LocalDate.now().toString())
                .toJobParameters();
        jobLauncher.run(dailyFragmentAggregationJob, jobParameters);
    }

//    @Scheduled(cron = "0 6 2 * * * ")
//    @Scheduled(cron = "0 5 2 L * * ")
    @Scheduled(cron = "0 27 2 * * * ")
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

    @Scheduled(cron = "0 28 2 * * * ")
    public void runYearlyFragmentAggregationJob() throws Exception{
        int year = LocalDate.now().minusMonths(1).getYear();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", LocalDateTime.now().toString())
                .addString("jobName", "YearlyFragmentAggregationJob")
                .addLong("year", (long) year)
                .toJobParameters();
        jobLauncher.run(yearlyFragmentAggregationJob, jobParameters);
    }

//    @Scheduled(cron = "0 0 */1 * * * ")
//    public void runDailyMemberAssetAggregationJob() throws Exception{
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("time", LocalDateTime.now().toString())
//                .addString("jobName", "DailyMemberAssetAggregationJob")
//                .toJobParameters();
//        jobLauncher.run(dailyMemberAssetAggregationJob, jobParameters);
//    }
}
