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
import java.util.*;

@Configuration
@AllArgsConstructor
public class OldUsersBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final ReviewRepository reviewRepository;
    private final UsedCouponRepository usedCouponRepository;
    private final UserRepository userRepository;

    private List<Long> deleteUserIds;
    private List<Long> deleteReservationIds;

    @Bean
    public Job oldUsersBatchJob() {

        deleteUserIds = new ArrayList<>();
        deleteReservationIds = new ArrayList<>();

        return new JobBuilder("oldUsersBatchJob", jobRepository)
                .start(oldUserStep())
                .next(oldUserReservationStep())
                .next(oldUserReservedSeatStep())
                .next(oldUserPaymentStep())
                .next(oldUserReviewStep())
                .next(oldUserUsedCouponStep())
                .listener(jobTimeExecutionListener)
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

        LocalDateTime targetDate = LocalDateTime.now().minusYears(3);

        return new RepositoryItemReaderBuilder<User>()
                .name("oldUserReader")
                .pageSize(50)
                .methodName("findAllOldUser")
                .arguments(targetDate)
                .repository(userRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> oldUserProcessor() {

        return user -> {
            deleteUserIds.add(user.getId());
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

    @Bean
    public Step oldUserReservationStep() {

        return new StepBuilder("oldUserReservationStep", jobRepository)
                .<Reservation, Reservation>chunk(500, platformTransactionManager)
                .reader(oldUserReservationReader())
                .processor(oldUserReservationProcessor())
                .writer(oldUserReservationWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Reservation> oldUserReservationReader() {

        return new RepositoryItemReaderBuilder<Reservation>()
                .name("oldUserReservationReader")
                .pageSize(50)
                .methodName("findByUserIdIn")
                .arguments(Collections.singletonList(deleteUserIds))
                .repository(reservationRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> oldUserReservationProcessor() {

        return reservation -> {
            deleteReservationIds.add(reservation.getId());
            return reservation;
        };
    }

    @Bean
    public RepositoryItemWriter<Reservation> oldUserReservationWriter() {

        return new RepositoryItemWriterBuilder<Reservation>()
                .repository(reservationRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldUserReservedSeatStep() {

        return new StepBuilder("oldUserReservedSeatStep", jobRepository)
                .<ReservedSeat, ReservedSeat>chunk(500, platformTransactionManager)
                .reader(oldUserReservedSeatReader())
                .writer(oldUserReservedSeatWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<ReservedSeat> oldUserReservedSeatReader() {

        return new RepositoryItemReaderBuilder<ReservedSeat>()
                .name("oldUserReservedSeatReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(reservedSeatRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<ReservedSeat> oldUserReservedSeatWriter() {

        return new RepositoryItemWriterBuilder<ReservedSeat>()
                .repository(reservedSeatRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldUserPaymentStep() {

        return new StepBuilder("oldUserPaymentStep", jobRepository)
                .<Payment, Payment>chunk(500, platformTransactionManager)
                .reader(oldUserPaymentReader())
                .writer(oldUserPaymentWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Payment> oldUserPaymentReader() {

        return new RepositoryItemReaderBuilder<Payment>()
                .name("oldUserPaymentReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(paymentRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Payment> oldUserPaymentWriter() {

        return new RepositoryItemWriterBuilder<Payment>()
                .repository(paymentRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldUserReviewStep() {

        return new StepBuilder("oldUserReviewStep", jobRepository)
                .<Review, Review>chunk(500, platformTransactionManager)
                .reader(oldUserReviewReader())
                .writer(oldUserReviewWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Review> oldUserReviewReader() {

        return new RepositoryItemReaderBuilder<Review>()
                .name("oldUserReviewReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(reviewRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<Review> oldUserReviewWriter() {

        return new RepositoryItemWriterBuilder<Review>()
                .repository(reviewRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldUserUsedCouponStep() {

        return new StepBuilder("oldUserUsedCouponStep", jobRepository)
                .<UsedCoupon, UsedCoupon>chunk(500, platformTransactionManager)
                .reader(oldUserUsedCouponReader())
                .writer(oldUserUsedCouponWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<UsedCoupon> oldUserUsedCouponReader() {

        return new RepositoryItemReaderBuilder<UsedCoupon>()
                .name("oldUserUsedCouponReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(deleteReservationIds))
                .repository(usedCouponRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<UsedCoupon> oldUserUsedCouponWriter() {

        return new RepositoryItemWriterBuilder<UsedCoupon>()
                .repository(usedCouponRepository)
                .methodName("delete")
                .build();
    }
}
