package com.example.projectbluebatch.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JobTimeExecutionListener implements JobExecutionListener {

    private LocalDateTime startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = LocalDateTime.now(); // 시작 시간 기록
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
//        System.out.println("--------------------------JPA--------------------------");
        System.out.println("--------------------------JDBC--------------------------");
        System.out.println("Job 시작 시간: " + startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        LocalDateTime endTime = LocalDateTime.now(); // 종료 시간 기록
        System.out.println("Job 종료 시간: " + endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 소요 시간 계산
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Job 수행 시간: " + duration.toHoursPart() + "시간 " +
                duration.toMinutesPart() + "분 " + duration.toSecondsPart() + "초");


        // 처리된 데이터 개수 출력
        long processedCount = jobExecution.getStepExecutions().stream()
                .filter(stepExecution -> stepExecution.getStatus() == BatchStatus.COMPLETED)
                .mapToLong(StepExecution::getWriteCount)
                .sum();
        System.out.println("처리된 데이터 개수: " + processedCount + "개");
    }
}