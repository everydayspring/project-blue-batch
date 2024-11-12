package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.entity.User;
import com.example.projectbluebatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.Random;

@Configuration
@AllArgsConstructor
public class SecondBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;
    private final UserRepository userRepository;

    @Bean
    public Job secondJob() {
        return new JobBuilder("secondJob", jobRepository)
                .start(secondStep())
//                .listener(jobTimeExecutionListener)z
                .build();
    }

    @Bean
    public Step secondStep() {
        return new StepBuilder("secondStep", jobRepository)
                .<User, User>chunk(500, platformTransactionManager)
                .reader(secondBeforeReader())
                .processor(secondMiddleProcessor())
                .writer(secondAfterWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<User> secondBeforeReader() {
        return new RepositoryItemReaderBuilder<User>()
                .name("secondBeforeReader")
                .pageSize(50)
                .methodName("findAll")
                .repository(userRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> secondMiddleProcessor() {
        Random random = new Random();

        return user -> {
            Long randomKakaoId = random.nextLong();
            user.setKakaoId(randomKakaoId);

            return user;
        };
    }

    @Bean
    public RepositoryItemWriter<User> secondAfterWriter() {
        return new RepositoryItemWriterBuilder<User>()
                .repository(userRepository)
                .methodName("save")
                .build();
    }
}
