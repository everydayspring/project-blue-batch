package com.example.projectbluebatch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
public class DB_JDBC_Thread_Batch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DB_JDBC_Thread_Batch(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                @Qualifier("dataDBSource") DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Bean
    public Job fourthJob() {
        return new JobBuilder("JDBC_THREAD_SPEED_TEST_JOB", jobRepository)
                .start(fourthStep())
                .build();
    }

    @Bean
    public Step fourthStep() {
        return new StepBuilder("JDBC_THREAD_SPEED_TEST_JOB_STEP", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Long.class);
                    Random random = new Random();

                    int batchSize = 1000;
                    for (int i = 0; i < userIds.size(); i += batchSize) {
                        List<Long> batchList = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

                        // 현재 스레드 이름 출력
//                        log.info("Executing batch update on thread: {}", Thread.currentThread().getName());

                        jdbcTemplate.batchUpdate("UPDATE users SET kakao_id = ? WHERE id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int j) throws SQLException {
                                ps.setLong(1, random.nextLong());
                                ps.setLong(2, batchList.get(j));
                            }

                            @Override
                            public int getBatchSize() {
                                return batchList.size();
                            }
                        });
                        batchList.clear();
                    }

                    return null;
                }, transactionManager)
                .taskExecutor(taskExecutor()) // 멀티스레딩을 위해 TaskExecutor 사용
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50); // 최소 스레드 개수, CPU 코어 수와 같게 설정
        executor.setMaxPoolSize(200);  // 최대 스레드 개수, CorePoolSize의 2배로 설정
        executor.setQueueCapacity(1000); // 큐 용량 설정
        executor.setThreadNamePrefix("Batch-Thread-");
        executor.initialize();
        return executor;
    }
}
