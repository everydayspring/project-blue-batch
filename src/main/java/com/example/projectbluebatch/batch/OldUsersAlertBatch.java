package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.config.SlackNotifier;
import com.example.projectbluebatch.entity.User;
import com.example.projectbluebatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class OldUsersAlertBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final UserRepository userRepository;
    private final SlackNotifier slackNotifier;

    private List<AlertUser> alertUsers;

    @Bean
    public Job oldUsersAlertBatchJob() {

        alertUsers = new ArrayList<>();

        return new JobBuilder("oldUsersAlertBatchJob", jobRepository)
                .start(oldUserAlertStep())
                .next(slackAlertStep())
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step oldUserAlertStep() {

        return new StepBuilder("oldUserAlertStep", jobRepository)
                .<User, User>chunk(500, platformTransactionManager)
                .reader(oldUserAlertReader())
                .processor(oldUserAlertProcessor())
                .writer(oldUserAlertWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<User> oldUserAlertReader() {

        LocalDateTime targetDate = LocalDateTime.now().minusYears(2);

        return new RepositoryItemReaderBuilder<User>()
                .name("oldUserAlertReader")
                .pageSize(50)
                .methodName("findAllOldUser")
                .arguments(targetDate)
                .repository(userRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> oldUserAlertProcessor() {

        return user -> {
            alertUsers.add(new AlertUser(user.getName(), user.getSlackId()));
            return user;
        };
    }

    @Bean
    public RepositoryItemWriter<User> oldUserAlertWriter() {

        return new RepositoryItemWriterBuilder<User>()
                .repository(userRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step slackAlertStep() {

        return new StepBuilder("slackAlertStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    alertUsers.forEach(alertUser -> {
                        String title = "[비밀번호 변경 안내]";

                        String userTag = "<@" + alertUser.getSlackId() + ">";

                        String message = String.format(
                                """
                                        %s \s
                                        %s 고객님 비밀번호를 변경한지 2년이 지났습니다.\s
                                        3년 이상 비밀번호를 변경하지 않는 경우 계정이 탈퇴됩니다.\s
                                        탈퇴 처리 이후 모든 예매내역 등은 복구할 수 없습니다.\s
                                        계정의 비밀번호를 변경해주시기 바랍니다.""",
                                userTag, alertUser.getUsername()
                        );
                        slackNotifier.sendMessage(title, message);
                    });
                    return RepeatStatus.FINISHED;
                }, new ResourcelessTransactionManager()) // 트랜잭션 없이 수행
                .build();
    }
}

@Getter
class AlertUser {

    private final String username;
    private final String slackId;

    public AlertUser(String username, String slackId) {

        this.username = username;
        this.slackId = slackId;
    }
}
