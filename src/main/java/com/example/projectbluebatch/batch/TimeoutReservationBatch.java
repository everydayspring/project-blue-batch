package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.entity.Payment;
import com.example.projectbluebatch.entity.Reservation;
import com.example.projectbluebatch.entity.ReservedSeat;
import com.example.projectbluebatch.entity.UsedCoupon;
import com.example.projectbluebatch.enums.PaymentStatus;
import com.example.projectbluebatch.repository.PaymentRepository;
import com.example.projectbluebatch.repository.ReservationRepository;
import com.example.projectbluebatch.repository.ReservedSeatRepository;
import com.example.projectbluebatch.repository.UsedCouponRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class TimeoutReservationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final UsedCouponRepository usedCouponRepository;

    private List<Long> timeoutReservationIds;

    @Bean
    public Job timeoutReservationBatchJob() {

        timeoutReservationIds = new ArrayList<>();

        return new JobBuilder("timeoutReservationBatchJob", jobRepository)
                .start(timeoutReservationStep())
                .next(timeoutReservationSeatStep())
                .next(timeoutReservationPaymentStep())
                .next(timeoutReservationUsedCouponStep())
                .listener(jobTimeExecutionListener)
                .build();
    }

    @Bean
    public Step timeoutReservationStep() {

        return new StepBuilder("timeoutReservationStep", jobRepository)
                .<Reservation, Reservation>chunk(500, platformTransactionManager)
                .reader(timeoutReservationReader())
                .processor(timeoutReservationProcessor())
                .writer(timeoutReservationWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Reservation> timeoutReservationReader() {

        LocalDate targetDate = LocalDate.now().minusDays(1);

        return new RepositoryItemReaderBuilder<Reservation>()
                .name("timeoutReservationReader")
                .pageSize(50)
                .methodName("findAllTimeoutReservations")
                .arguments(targetDate)
                .repository(reservationRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> timeoutReservationProcessor() {

        return reservation -> {
            timeoutReservationIds.add(reservation.getId());
            reservation.canceled();
            return reservation;
        };
    }

    @Bean
    public RepositoryItemWriter<Reservation> timeoutReservationWriter() {

        return new RepositoryItemWriterBuilder<Reservation>()
                .repository(reservationRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step timeoutReservationSeatStep() {

        return new StepBuilder("timeoutReservationSeatStep", jobRepository)
                .<ReservedSeat, ReservedSeat>chunk(500, platformTransactionManager)
                .reader(timeoutReservationSeatReader())
                .writer(timeoutReservationSeatWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<ReservedSeat> timeoutReservationSeatReader() {

        return new RepositoryItemReaderBuilder<ReservedSeat>()
                .name("timeoutReservationSeatReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(timeoutReservationIds))
                .repository(reservedSeatRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<ReservedSeat> timeoutReservationSeatWriter() {

        return new RepositoryItemWriterBuilder<ReservedSeat>()
                .repository(reservedSeatRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step timeoutReservationPaymentStep() {

        return new StepBuilder("timeoutReservationPaymentStep", jobRepository)
                .<Payment, Payment>chunk(500, platformTransactionManager)
                .reader(timeoutReservationPaymentReader())
                .processor(timeoutReservationPaymentProcessor())
                .writer(timeoutReservationPaymentWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Payment> timeoutReservationPaymentReader() {

        return new RepositoryItemReaderBuilder<Payment>()
                .name("timeoutReservationPaymentReader")
                .pageSize(50)
                .methodName("findByStatusAndReservationIdIn")
                .arguments(PaymentStatus.READY, timeoutReservationIds) // 수정된 부분
                .repository(paymentRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Payment, Payment> timeoutReservationPaymentProcessor() {

        return payment -> {
            payment.canceled();
            return payment;
        };
    }

    @Bean
    public RepositoryItemWriter<Payment> timeoutReservationPaymentWriter() {

        return new RepositoryItemWriterBuilder<Payment>()
                .repository(paymentRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step timeoutReservationUsedCouponStep() {

        return new StepBuilder("timeoutReservationUsedCouponStep", jobRepository)
                .<UsedCoupon, UsedCoupon>chunk(500, platformTransactionManager)
                .reader(timeoutReservationUsedCouponReader())
                .writer(timeoutReservationUsedCouponWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<UsedCoupon> timeoutReservationUsedCouponReader() {

        return new RepositoryItemReaderBuilder<UsedCoupon>()
                .name("timeoutReservationUsedCouponReader")
                .pageSize(50)
                .methodName("findByReservationIdIn")
                .arguments(Collections.singletonList(timeoutReservationIds))
                .repository(usedCouponRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemWriter<UsedCoupon> timeoutReservationUsedCouponWriter() {

        return new RepositoryItemWriterBuilder<UsedCoupon>()
                .repository(usedCouponRepository)
                .methodName("delete")
                .build();
    }
}
