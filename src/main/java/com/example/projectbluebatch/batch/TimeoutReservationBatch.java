package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.enums.PaymentStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TimeoutReservationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JdbcTemplate jdbcTemplate;

    public TimeoutReservationBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    private List<Long> timeoutReservationIds = new ArrayList<>();

    @Bean
    public Job timeoutReservationBatchJob() {
        return new JobBuilder("timeoutReservationBatchJob", jobRepository)
                .start(timeoutReservationStep())
                .next(timeoutReservationSeatStep())
                .next(timeoutReservationPaymentStep())
                .next(timeoutReservationUsedCouponStep())
                .build();
    }

    @Bean
    public Step timeoutReservationStep() {
        return new StepBuilder("timeoutReservationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate targetDate = LocalDate.now().minusDays(1);
                    timeoutReservationIds = jdbcTemplate.queryForList(
                            "SELECT id FROM reservations WHERE DATE(modified_at) <= ? AND status = 'PENDING'",
                            Long.class, targetDate
                    );

                    if (!CollectionUtils.isEmpty(timeoutReservationIds)) {
                        jdbcTemplate.batchUpdate("UPDATE reservations SET status = 'CANCELED' WHERE id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, timeoutReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return timeoutReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step timeoutReservationSeatStep() {
        return new StepBuilder("timeoutReservationSeatStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(timeoutReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM reserved_seats WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, timeoutReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return timeoutReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step timeoutReservationPaymentStep() {
        return new StepBuilder("timeoutReservationPaymentStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(timeoutReservationIds)) {

                        String placeholders = String.join(",", timeoutReservationIds.stream().map(id -> "?").toArray(String[]::new));
                        String query = String.format(
                                "UPDATE payments SET status = 'CANCELED' WHERE status = ? AND reservation_id IN (%s)", placeholders
                        );

                        jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {

                                ps.setString(1, PaymentStatus.READY.toString());

                                for (int j = 0; j < timeoutReservationIds.size(); j++) {
                                    ps.setLong(j + 2, timeoutReservationIds.get(j));
                                }
                            }

                            @Override
                            public int getBatchSize() {
                                return 1;
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }


    @Bean
    public Step timeoutReservationUsedCouponStep() {
        return new StepBuilder("timeoutReservationUsedCouponStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(timeoutReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM used_coupon WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, timeoutReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return timeoutReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }
}
