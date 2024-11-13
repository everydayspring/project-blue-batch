//package com.example.projectbluebatch.config;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.boot.autoconfigure.batch.BatchProperties;
//import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//import java.util.Collection;
//
//@Configuration
//@EnableConfigurationProperties(BatchProperties.class)
//public class BatchConfig {
//
//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
//    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
//                                                                     JobRepository jobRepository, BatchProperties properties, Collection<Job> jobs) {
//        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
//        String jobNames = properties.getJob().getName();
//        if (StringUtils.hasText(jobNames)) {
//            // 로그 추가하여 확인
//            System.out.println("Attempting to run job: " + jobNames);
//
//            if (jobs.stream().map(Job::getName).noneMatch(s -> s.equals(jobNames))){
//                throw new IllegalArgumentException(jobNames + "는 등록되지 않은 job name입니다. job name을 확인하세요.");
//            }
//            runner.setJobName(jobNames);
//        } else {
//            System.out.println("No job name specified.");
//        }
//        return runner;
//    }
//}