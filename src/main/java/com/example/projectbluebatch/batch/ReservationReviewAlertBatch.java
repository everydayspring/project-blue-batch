package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.config.SlackNotifier;
import com.example.projectbluebatch.dto.ReservationReviewAlertInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ReservationReviewAlertBatch {

    private final JobRepository jobRepository;
    private final JobTimeExecutionListener jobTimeExecutionListener;
    private final SlackNotifier slackNotifier;
    private final JdbcTemplate jdbcTemplate;

    private List<ReservationReviewAlertInfo> alertInfos;

    @Autowired
    public ReservationReviewAlertBatch(JobRepository jobRepository, JobTimeExecutionListener jobTimeExecutionListener,
                                       SlackNotifier slackNotifier, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.jobTimeExecutionListener = jobTimeExecutionListener;
        this.slackNotifier = slackNotifier;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    @Bean
    public Job reservationReviewAlertBatchJob() {
        List<Long> roundIds = jdbcTemplate.queryForList(
                """
                        SELECT r.id
                        FROM rounds r
                        WHERE DATE(r.date) = DATE(?)
                        """,
                new Object[]{LocalDateTime.now()},
                Long.class
        );

        if (!roundIds.isEmpty()) {
            String roundIdsInClause = roundIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            alertInfos = jdbcTemplate.query(
                    "SELECT res.id AS reservationId, u.id AS userId, u.name AS userName, u.slack_id AS slackId, " +
                            "p.id AS performanceId, p.title AS performanceTitle, r.date AS roundDate, " +
                            "h.id AS hallId, h.name AS hallName " +
                            "FROM reservations res " +
                            "JOIN users u ON res.user_id = u.id " +
                            "JOIN performances p ON res.performance_id = p.id " +
                            "JOIN rounds r ON res.round_id = r.id " +
                            "JOIN halls h ON p.hall_id = h.id " +
                            "WHERE res.round_id IN (" + roundIdsInClause + ") " +
                            "AND res.status = 'COMPLETED'",
                    (rs, rowNum) -> new ReservationReviewAlertInfo(
                            rs.getLong("userId"),
                            rs.getString("userName"),
                            rs.getString("slackId"),
                            rs.getLong("performanceId"),
                            rs.getString("performanceTitle"),
                            rs.getLong("hallId"),
                            rs.getTimestamp("roundDate").toLocalDateTime(),
                            rs.getString("hallName")
                    )
            );
        } else {
            alertInfos = List.of();
        }

        return new JobBuilder("reservationReviewAlertBatchJob", jobRepository)
                .start(reservationReviewAlertSlackStep())
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step reservationReviewAlertSlackStep() {
        return new StepBuilder("reservationReviewAlertSlackStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    alertInfos.forEach(alertInfo -> {
                        String title = "[관람일 D-Day]";
                        String userTag = alertInfo.getSlackId() != null && !alertInfo.getSlackId().isEmpty()
                                ? "<@" + alertInfo.getSlackId() + ">"
                                : "";
                        String message = String.format(
                                """
                                        %s
                                        - %s 고객님, 관람은 즐거우셨나요? 관람평을 등록해주세요! \s
                                        - 상품명: %s \s
                                        - 일시: %s \s
                                        - 관람장소: %s
                                        """,
                                userTag,
                                alertInfo.getUserName(),
                                alertInfo.getPerformanceTitle(),
                                alertInfo.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                alertInfo.getHallName()
                        );
                        slackNotifier.sendMessage(title, message);
                    });
                    return RepeatStatus.FINISHED;
                }, new ResourcelessTransactionManager())
                .build();
    }
}
