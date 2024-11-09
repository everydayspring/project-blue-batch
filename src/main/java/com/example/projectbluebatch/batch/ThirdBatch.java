package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListenerJdbc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Random;

@Configuration
public class ThirdBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListenerJdbc jobTimeExecutionListenerJdbc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThirdBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, JobTimeExecutionListenerJdbc jobTimeExecutionListenerJdbc, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.jobTimeExecutionListenerJdbc = jobTimeExecutionListenerJdbc;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    @Bean
    public Job thirdJob() {
        return new JobBuilder("thirdJob", jobRepository)
                .start(thirdStep())
                .listener(jobTimeExecutionListenerJdbc)
                .build();
    }

    @Bean
    public Step thirdStep() {
        return new StepBuilder("thirdStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Long.class);
                    Random random = new Random();

                    userIds.forEach(id -> {
                        Long randomKakaoId = random.nextLong();
                        jdbcTemplate.update("UPDATE users SET kakao_id = ? WHERE id = ?", randomKakaoId, id);
                    });

                    return null;
                }, platformTransactionManager)
                .build();
    }
}
