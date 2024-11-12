package com.example.projectbluebatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
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

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

@Configuration
public class ThirdBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThirdBatch(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      @Qualifier("dataDBSource") DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Bean
    public Job thirdJob() {
        return new JobBuilder("thirdJob", jobRepository)
                .start(thirdStep())
                // jobTimeExecutionListener 추가
                .build();
    }

//    @Bean
//    public Step thirdStep() {
//        return new StepBuilder("thirdStep", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    // user IDs 조회
//                    List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Long.class);
//                    Random random = new Random();
//
//                    if (!userIds.isEmpty()) {
//                        // BatchPreparedStatementSetter를 사용하여 1개씩 배치 처리
//                        jdbcTemplate.batchUpdate("UPDATE users SET kakao_id = ? WHERE id = ?", new BatchPreparedStatementSetter() {
//                            @Override
//                            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                                ps.setLong(1, random.nextLong());
//                                ps.setLong(2, userIds.get(i));
//                            }
//
//                            @Override
//                            public int getBatchSize() {
//                                return userIds.size();
//                            }
//                        });
//                    }
//
//                    return null;
//                }, transactionManager)
//                .build();
//    }

    @Bean
    public Step thirdStep() {
        return new StepBuilder("thirdStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // user IDs 조회
                    List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Long.class);
                    Random random = new Random();

                    int batchSize = 500;
                    // 배치 업데이트를 500개씩 처리
                    for (int i = 0; i < userIds.size(); i += batchSize) {
                        List<Long> batchList = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

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
                    batchList.clear(); // 최고의 튜터
                    }

                    return null;
                }, transactionManager)
//                .taskExecutor(taskExecutor()) // 멀티스레딩을 위해 TaskExecutor 사용
                .build();
    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(16); // 최소 스레드 개수, CPU 코어 수와 같게 설정
//        executor.setMaxPoolSize(32);  // 최대 스레드 개수, CorePoolSize의 2배로 설정
//        executor.setQueueCapacity(150); // 큐 용량 설정
//        executor.setThreadNamePrefix("Batch-Thread-");
//        executor.initialize();
//        return executor;
//    }
}
