package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
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
import java.util.ArrayList;
import java.util.List;

@Configuration
public class OldPerformancesBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;
    private final JdbcTemplate jdbcTemplate;

    public OldPerformancesBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, JobTimeExecutionListener jobTimeExecutionListener, @Qualifier("dataDBSource") DataSource dataDBSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.jobTimeExecutionListener = jobTimeExecutionListener;
        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
    }

    private List<Long> deletePerformanceIds = new ArrayList<>();
    private List<Long> deleteReservationIds = new ArrayList<>();

    @Bean
    public Job oldPerformancesBatchJob() {
        return new JobBuilder("oldPerformancesBatchJob", jobRepository)
                .start(oldPerformanceStep())
                .next(oldPerformanceReservationStep())
                .next(oldPerformanceReservedSeatStep())
                .next(oldPerformancePaymentStep())
                .next(oldPerformanceReviewStep())
                .next(oldPerformanceUsedCouponStep())
                .next(oldPerformanceRoundStep())
                .next(oldPerformancePerformerStep())
                .next(oldPerformancePosterStep())
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step oldPerformanceStep() {
        return new StepBuilder("oldPerformanceStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDateTime targetDate = LocalDateTime.now().minusYears(10);
                    deletePerformanceIds = jdbcTemplate.queryForList(
                            "SELECT id FROM performances WHERE end_date <= ?", Long.class, targetDate
                    );

                    if (!CollectionUtils.isEmpty(deletePerformanceIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM performances WHERE id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deletePerformanceIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deletePerformanceIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldPerformanceReservationStep() {
        return new StepBuilder("oldPerformanceReservationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deletePerformanceIds)) {
                        deleteReservationIds = jdbcTemplate.queryForList(
                                "SELECT id FROM reservations WHERE performance_id IN (?)", Long.class, deletePerformanceIds.toArray()
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
    public Step oldPerformanceReservedSeatStep() {
        return new StepBuilder("oldPerformanceReservedSeatStep", jobRepository)
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
    public Step oldPerformancePaymentStep() {
        return new StepBuilder("oldPerformancePaymentStep", jobRepository)
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
    public Step oldPerformanceReviewStep() {
        return new StepBuilder("oldPerformanceReviewStep", jobRepository)
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
    public Step oldPerformanceUsedCouponStep() {
        return new StepBuilder("oldPerformanceUsedCouponStep", jobRepository)
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

    @Bean
    public Step oldPerformanceRoundStep() {
        return new StepBuilder("oldPerformanceRoundStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deletePerformanceIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM rounds WHERE performance_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deletePerformanceIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deletePerformanceIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldPerformancePerformerStep() {
        return new StepBuilder("oldPerformancePerformerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deletePerformanceIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM performer_performance WHERE performance_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deletePerformanceIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deletePerformanceIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step oldPerformancePosterStep() {
        return new StepBuilder("oldPerformancePosterStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (!CollectionUtils.isEmpty(deletePerformanceIds)) {
                        jdbcTemplate.batchUpdate("DELETE FROM posters WHERE performance_id = ?", new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, deletePerformanceIds.get(i));
                            }

                            @Override
                            public int getBatchSize() {
                                return deletePerformanceIds.size();
                            }
                        });
                    }
                    return null;
                }, platformTransactionManager)
                .build();
    }
}
