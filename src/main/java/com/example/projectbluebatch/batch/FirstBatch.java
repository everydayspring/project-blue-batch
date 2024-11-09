package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.entity.User;
import com.example.projectbluebatch.entity.UserCopy;
import com.example.projectbluebatch.repository.UserCopyRepository;
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

@Configuration
@AllArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final UserRepository userRepository;
    private final UserCopyRepository userCopyRepository;

    @Bean
    public Job firstJob() {

        return new JobBuilder("firstJob", jobRepository)
                .start(firstStep())
//                .next() // job이 여러개인 경우
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step firstStep() {

        return new StepBuilder("firstStep", jobRepository)
                .<User, UserCopy> chunk(500, platformTransactionManager)
                // chunk -> 몇개씩 끊어서 작업할건지
                // 데이터 양과 메모리 성능을 고려해서 결정함
                // 너무 작으면 오버헤드 발생, 너무 크면 자원 사용에 대한 비용과 실패 부담이 큼
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<User> beforeReader() {

        return new RepositoryItemReaderBuilder<User>()
                .name("beforeReader")
                .pageSize(50) // 데이터 읽을때 끊어서 읽을때
                .methodName("findAll") // JPA 쿼리 이름
                .repository(userRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    ItemProcessor<User, UserCopy> middleProcessor() {
        return new ItemProcessor<User, UserCopy>() {
            @Override
            public UserCopy process(User user) throws Exception {

                UserCopy copy = new UserCopy();
                copy.copy(user);

                return copy;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<UserCopy> afterWriter() {

        return new RepositoryItemWriterBuilder<UserCopy>()
                .repository(userCopyRepository)
                .methodName("save")
                .build();
    }
}