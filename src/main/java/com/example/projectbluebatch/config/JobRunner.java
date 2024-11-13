package com.example.projectbluebatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
        String port = System.getProperty("server.port", "8081");  // 기본값 8081

        if (jobName != null && jobs.containsKey(jobName)) {
            Job job = jobs.get(jobName);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            System.out.println("Attempting to run job: " + jobName);

            try {
                jobLauncher.run(job, jobParameters);
                System.out.println("Job " + jobName + " completed successfully.");
                System.exit(0);
            } catch (Exception e) {
                System.err.println("Job " + jobName + " failed: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("Job name not found or specified.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Map<String, Object> properties = new HashMap<>();
        String port = System.getProperty("server.port", "8081");
        properties.put("server.port", port);

        SpringApplication app = new SpringApplication(JobRunner.class);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
