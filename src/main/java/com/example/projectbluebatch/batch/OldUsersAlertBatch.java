package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.config.SlackNotifier;
import com.example.projectbluebatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class OldUsersAlertBatch {

    private final JobRepository jobRepository;
    private final JobTimeExecutionListener jobTimeExecutionListener;
    private final SlackNotifier slackNotifier;
    private final JdbcTemplate jdbcTemplate;

    public OldUsersAlertBatch(JobRepository jobRepository, JobTimeExecutionListener jobTimeExecutionListener, SlackNotifier slackNotifier, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.jobTimeExecutionListener = jobTimeExecutionListener;
        this.slackNotifier = slackNotifier;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    @Bean
    public Job oldUsersAlertBatchJob() {
        return new JobBuilder("oldUsersAlertBatchJob", jobRepository)
                .start(oldUserAlertStep())
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step oldUserAlertStep() {
        return new StepBuilder("oldUserAlertStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDateTime targetDate = LocalDateTime.now().minusYears(2);

                    String query = "SELECT * FROM users WHERE modified_at < ?";
                    JdbcCursorItemReader<User> reader = new JdbcCursorItemReaderBuilder<User>()
                            .dataSource(jdbcTemplate.getDataSource())
                            .name("oldUserAlertReader") // 이름 추가
                            .sql(query)
                            .preparedStatementSetter((ps) -> ps.setObject(1, targetDate))
                            .rowMapper((rs, rowNum) -> {
                                User user = new User();
                                user.setId(rs.getLong("id"));
                                user.setName(rs.getString("name"));
                                user.setSlackId(rs.getString("slack_id"));
                                return user;
                            })
                            .build();

                    reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

                    User user;
                    while ((user = reader.read()) != null) {
                        String title = "[비밀번호 변경 안내]";
                        String userTag = user.getSlackId() != null && !user.getSlackId().isEmpty() ? "<@" + user.getSlackId() + ">" : "";
                        String message = String.format(
                                """
                                        %s \s
                                        %s 고객님 비밀번호를 변경한지 2년이 지났습니다.\s
                                        3년 이상 비밀번호를 변경하지 않는 경우 계정이 탈퇴됩니다.\s
                                        탈퇴 처리 이후 모든 예매내역 등은 복구할 수 없습니다.\s
                                        계정의 비밀번호를 변경해주시기 바랍니다.""",
                                userTag, user.getName()
                        );
                        slackNotifier.sendMessage(title, message);
                    }

                    reader.close();
                    return RepeatStatus.FINISHED;
                }, new ResourcelessTransactionManager())
                .build();
    }
}
