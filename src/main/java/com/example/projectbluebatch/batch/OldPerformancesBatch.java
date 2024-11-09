package com.example.projectbluebatch.batch;


import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.entity.*;
import com.example.projectbluebatch.repository.*;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class OldPerformancesBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final PaymentRepository paymentRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformerPerformanceRepository performerPerformanceRepository;
    private final PosterRepository posterRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final ReviewRepository reviewRepository;
    private final RoundRepository roundRepository;
    private final UsedCouponRepository usedCouponRepository;

    private List<Long> deletePerformanceIds;
    private List<Long> deleteReservationIds;

    @Bean
    public Job OldPerformancesJob() {

        deletePerformanceIds = new ArrayList<>();
        deleteReservationIds = new ArrayList<>();

        return new JobBuilder("OldPerformancesBatchJob", jobRepository)
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
                .<Performance, Performance>chunk(500, platformTransactionManager)
                .reader(oldPerformanceReader())
                .processor(oldPerformanceProcessor())
                .writer(oldPerformanceWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Performance> oldPerformanceReader() {

        LocalDateTime targetDate = LocalDateTime.now().minusYears(10);

        return new RepositoryItemReaderBuilder<Performance>()
                .name("oldPerformanceReader")
                .pageSize(50)
                .methodName("findAllOldPerformance")
                .arguments(targetDate)
                .repository(performanceRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Performance, Performance> oldPerformanceProcessor() {

        return performance -> {
            deletePerformanceIds.add(performance.getId());
            return performance;
        };
    }

    @Bean
    public RepositoryItemWriter<Performance> oldPerformanceWriter() {

        return new RepositoryItemWriterBuilder<Performance>()
                .repository(performanceRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformanceReservationStep() {

        return new StepBuilder("oldPerformanceReservationStep", jobRepository)
                .<Reservation, Reservation>chunk(500, platformTransactionManager)
                .reader(oldPerformanceReservationReader())
                .processor(oldPerformanceReservationProcessor())
                .writer(oldPerformanceReservationWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Reservation> oldPerformanceReservationReader() {

        return new RepositoryItemReaderBuilder<Reservation>()
                .name("oldPerformanceReservationReader")
                .pageSize(50)
                .methodName("findByPerformanceIdIn")
                .arguments(Collections.singletonList(deletePerformanceIds))
                .repository(reservationRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> oldPerformanceReservationProcessor() {

        return reservation -> {
            deleteReservationIds.add(reservation.getId());
            return reservation;
        };
    }

    @Bean
    public RepositoryItemWriter<Reservation> oldPerformanceReservationWriter() {

        return new RepositoryItemWriterBuilder<Reservation>()
                .repository(reservationRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformanceReservedSeatStep() {

        return new StepBuilder("oldPerformanceReservedSeatStep", jobRepository)
                .<ReservedSeat, ReservedSeat>chunk(500, platformTransactionManager)
                .reader(oldPerformanceReservedSeatReader())
                .writer(oldPerformanceReservedSeatWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<ReservedSeat> oldPerformanceReservedSeatReader() {

        return new RepositoryItemReaderBuilder<ReservedSeat>()
                .name("oldPerformanceReservedSeatReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(reservedSeatRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<ReservedSeat> oldPerformanceReservedSeatWriter() {

        return new RepositoryItemWriterBuilder<ReservedSeat>()
                .repository(reservedSeatRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformancePaymentStep() {

        return new StepBuilder("oldPerformancePaymentStep", jobRepository)
                .<Payment, Payment>chunk(500, platformTransactionManager)
                .reader(oldPerformancePaymentReader())
                .writer(oldPerformancePaymentWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Payment> oldPerformancePaymentReader() {

        return new RepositoryItemReaderBuilder<Payment>()
                .name("oldPerformancePaymentReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(paymentRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Payment> oldPerformancePaymentWriter() {

        return new RepositoryItemWriterBuilder<Payment>()
                .repository(paymentRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformanceReviewStep() {

        return new StepBuilder("oldPerformanceReviewStep", jobRepository)
                .<Review, Review>chunk(500, platformTransactionManager)
                .reader(oldPerformanceReviewReader())
                .writer(oldPerformanceReviewWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Review> oldPerformanceReviewReader() {

        return new RepositoryItemReaderBuilder<Review>()
                .name("oldPerformanceReviewReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(reviewRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Review> oldPerformanceReviewWriter() {

        return new RepositoryItemWriterBuilder<Review>()
                .repository(reviewRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformanceUsedCouponStep() {

        return new StepBuilder("oldPerformanceUsedCouponStep", jobRepository)
                .<UsedCoupon, UsedCoupon>chunk(500, platformTransactionManager)
                .reader(oldPerformanceUsedCouponReader())
                .writer(oldPerformanceUsedCouponWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<UsedCoupon> oldPerformanceUsedCouponReader() {

        return new RepositoryItemReaderBuilder<UsedCoupon>()
                .name("oldPerformanceUsedCouponReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(usedCouponRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<UsedCoupon> oldPerformanceUsedCouponWriter() {

        return new RepositoryItemWriterBuilder<UsedCoupon>()
                .repository(usedCouponRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformanceRoundStep() {

        return new StepBuilder("oldPerformanceRoundStep", jobRepository)
                .<Round, Round>chunk(500, platformTransactionManager)
                .reader(oldPerformanceRoundReader())
                .writer(oldPerformanceRoundWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Round> oldPerformanceRoundReader() {

        return new RepositoryItemReaderBuilder<Round>()
                .name("oldPerformanceRoundReader")
                .pageSize(50)
                .methodName("findByPerformanceIdIn")
                .arguments(Collections.singletonList(deletePerformanceIds))
                .repository(roundRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Round> oldPerformanceRoundWriter() {

        return new RepositoryItemWriterBuilder<Round>()
                .repository(roundRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformancePerformerStep() {

        return new StepBuilder("oldPerformancePerformerStep", jobRepository)
                .<PerformerPerformance, PerformerPerformance>chunk(500, platformTransactionManager)
                .reader(oldPerformancePerformerReader())
                .writer(oldPerformancePerformerWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<PerformerPerformance> oldPerformancePerformerReader() {

        return new RepositoryItemReaderBuilder<PerformerPerformance>()
                .name("oldPerformancePerformerReader")
                .pageSize(50)
                .methodName("findByPerformanceIdIn")
                .arguments(Collections.singletonList(deletePerformanceIds))
                .repository(performerPerformanceRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<PerformerPerformance> oldPerformancePerformerWriter() {

        return new RepositoryItemWriterBuilder<PerformerPerformance>()
                .repository(performerPerformanceRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPerformancePosterStep() {

        return new StepBuilder("oldPerformancePosterStep", jobRepository)
                .<Poster, Poster>chunk(500, platformTransactionManager)
                .reader(oldPerformancePosterReader())
                .writer(oldPerformancePosterWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Poster> oldPerformancePosterReader() {

        return new RepositoryItemReaderBuilder<Poster>()
                .name("oldPerformancePosterReader")
                .pageSize(50)
                .methodName("findByPerformanceIdIn")
                .arguments(Collections.singletonList(deletePerformanceIds))
                .repository(posterRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Poster> oldPerformancePosterWriter() {

        return new RepositoryItemWriterBuilder<Poster>()
                .repository(posterRepository)
                .methodName("delete")
                .build();
    }
}