package com.example.projectbluebatch.batch;

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

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class OldRecordsBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final UserRepository userRepository;

    @Bean
    public Job OldRecordsBatchJob() {

        return new JobBuilder("OldRecordsBatchJop", jobRepository)
                .start(oldUserStep())
//                .next() // job이 여러개인 경우
                .build();
    }

    @Bean
    public Step oldUserStep() {

        return new StepBuilder("oldUserStep", jobRepository)
                .<User, User>chunk(500, platformTransactionManager)
                // chunk -> 몇개씩 끊어서 작업할건지
                // 데이터 양과 메모리 성능을 고려해서 결정함
                // 너무 작으면 오버헤드 발생, 너무 크면 자원 사용에 대한 비용과 실패 부담이 큼
                .reader(oldUserReader())
                .processor(oldUserProcessor())
                .writer(oldUserWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<User> oldUserReader() {
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);

        return new RepositoryItemReaderBuilder<User>()
                .name("oldUserReader")
                .pageSize(10)
                .methodName("findAllCreatedBefore")
                .arguments(threeYearsAgo)
                .repository(userRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> oldUserProcessor() {
        return user -> {
            user.userDeleted();
            return user;
        };
    }

    @Bean
    public RepositoryItemWriter<User> oldUserWriter() {
        return new RepositoryItemWriterBuilder<User>()
                .repository(userRepository)
                .methodName("save")
                .build();
    }
}
