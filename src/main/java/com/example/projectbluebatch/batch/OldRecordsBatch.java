package com.example.projectbluebatch.batch;

import com.example.projectbluebatch.config.JobTimeExecutionListener;
import com.example.projectbluebatch.entity.Payment;
import com.example.projectbluebatch.entity.Reservation;
import com.example.projectbluebatch.entity.ReservedSeat;
import com.example.projectbluebatch.entity.User;
import com.example.projectbluebatch.repository.PaymentRepository;
import com.example.projectbluebatch.repository.ReservationRepository;
import com.example.projectbluebatch.repository.ReservedSeatRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@AllArgsConstructor
public class OldRecordsBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobTimeExecutionListener jobTimeExecutionListener;

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final UserRepository userRepository;

    private List<Long> deleteUserIds;

    private List<Long> deleteReservationIds;

    @Bean
    public Job OldRecordsBatchJob() {

        deleteUserIds = new ArrayList<>();
        deleteReservationIds = new ArrayList<>();

        return new JobBuilder("OldRecordsBatchJob", jobRepository)
                .start(oldUserStep())
                .next(oldUserReservationStep())
                .next(oldUserReservedSeatStep())
                .next(oldReservationStep())
                .next(oldPaymentStep())
                .listener(jobTimeExecutionListener) // Listener 등록
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
    public Step oldReservationStep() {

        return new StepBuilder("oldReservationStep", jobRepository)
                .<Reservation, Reservation>chunk(500, platformTransactionManager)
                .reader(oldReservationReader())
                .writer(oldReservationWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Reservation> oldReservationReader() {

        LocalDateTime targetDate = LocalDateTime.now().minusYears(10);

        return new RepositoryItemReaderBuilder<Reservation>()
                .name("oldReservationReader")
                .pageSize(50)
                .methodName("findAllOldReservation")
                .arguments(targetDate)
                .repository(reservationRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public RepositoryItemWriter<Reservation> oldReservationWriter() {

        return new RepositoryItemWriterBuilder<Reservation>()
                .repository(reservationRepository)
                .methodName("delete")
                .build();
    }

    @Bean
    public Step oldPaymentStep() {

        return new StepBuilder("oldPaymentStep", jobRepository)
                .<Payment, Payment>chunk(500, platformTransactionManager)
                .reader(oldPaymentReader())
                .writer(oldPaymentWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Payment> oldPaymentReader() {

        LocalDateTime targetDate = LocalDateTime.now().minusYears(10);

        return new RepositoryItemReaderBuilder<Payment>()
                .name("oldPaymentReader")
                .pageSize(50)
                .methodName("findAllOldPayment")
                .arguments(targetDate)
                .repository(paymentRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }


    @Bean
    public RepositoryItemWriter<Payment> oldPaymentWriter() {

        return new RepositoryItemWriterBuilder<Payment>()
                .repository(paymentRepository)
                .methodName("delete")
                .build();
    }
}
