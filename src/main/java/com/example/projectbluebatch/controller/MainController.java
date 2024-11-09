package com.example.projectbluebatch.controller;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@ResponseBody
@AllArgsConstructor
public class MainController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

        return "First job executed";
    }

    @GetMapping("/oldUsersBatch")
    public String oldUsersBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("oldUsersBatchJob"), jobParameters);

        return "Old users batch job executed";
    }

    @GetMapping("/oldPerformancesBatch")
    public String oldPerformancesBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("oldPerformancesBatchJob"), jobParameters);

        return "Old performances batch job executed";
    }

    @GetMapping("/reservationCancellationBatch")
    public String reservationCancellationBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("reservationCancellationBatchJob"), jobParameters);

        return "Old reservationCancellation batch job executed";
    }


}
