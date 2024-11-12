package com.example.projectbluebatch.batch;

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
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class OldUsersBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JdbcTemplate jdbcTemplate;

    public OldUsersBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    private List<Long> deleteUserIds;
    private List<Long> deleteReservationIds;

    @Bean
    public Job oldUsersBatchJob() {
        return new JobBuilder("oldUsersBatchJob", jobRepository)
                .start(oldUserStep())
                .next(oldUserReservationStep())
                .next(oldUserReservedSeatStep())
                .next(oldUserPaymentStep())
                .next(oldUserReviewStep())
                .next(oldUserUsedCouponStep())
                .build();
    }

    @Bean
    public Step oldUserStep() {
        return new StepBuilder("oldUserStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDateTime targetDate = LocalDateTime.now().minusYears(3);
                    deleteUserIds = jdbcTemplate.queryForList(
                            "SELECT id FROM users WHERE modified_at <= ?",
                            Long.class, targetDate
                    );

                    if (!CollectionUtils.isEmpty(deleteUserIds)) {
                        jdbcTemplate.batchUpdate("UPDATE users SET is_deleted = TRUE WHERE id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deleteUserIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deleteUserIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldUserReservationStep() {
        return new StepBuilder("oldUserReservationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deleteUserIds)) {
                        deleteReservationIds = jdbcTemplate.queryForList(
                                "SELECT id FROM reservations WHERE user_id IN (?)",
                                Long.class, deleteUserIds.toArray()
                        );

                        if (!CollectionUtils.isEmpty(deleteReservationIds)) {
                            jdbcTemplate.batchUpdate("DELETE FROM reservations WHERE id = ?", new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    ps.setLong(1, deleteReservationIds.get(i));
                                }

                                @Override
                                public int getBatchSize() {
                                    return deleteReservationIds.size();
                                }
                            });
                        }
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldUserReservedSeatStep() {
        return new StepBuilder("oldUserReservedSeatStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deleteReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM reserved_seats WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deleteReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deleteReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldUserPaymentStep() {
        return new StepBuilder("oldUserPaymentStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deleteReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM payments WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deleteReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deleteReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldUserReviewStep() {
        return new StepBuilder("oldUserReviewStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deleteReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM reviews WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deleteReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deleteReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldUserUsedCouponStep() {
        return new StepBuilder("oldUserUsedCouponStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deleteReservationIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM used_coupon WHERE reservation_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deleteReservationIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deleteReservationIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }
}
