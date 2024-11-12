package com.example.projectbluebatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class JobRunner implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Map<String, Job> jobs;

    @Override
    public void run(String... args) throws Exception {
        String jobName = System.getProperty("job.name");

        if (jobName != null && jobs.containsKey(jobName)) {
            Job job = jobs.get(jobName);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis()) // 유니크 ID
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
        } else {
            System.out.println("Job name not found or specified.");
        }
    }
}
