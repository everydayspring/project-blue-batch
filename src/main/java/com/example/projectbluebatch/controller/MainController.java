package com.example.projectbluebatch.controller;

import com.example.projectbluebatch.entity.User;
import com.example.projectbluebatch.enums.UserRole;
import com.example.projectbluebatch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Controller
@ResponseBody
@AllArgsConstructor
public class MainController {

    private final UserRepository userRepository;

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/first")
    public String firstApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

        return "First job executed";
    }

    @GetMapping("/second")
    public String secondApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("secondJob"), jobParameters);

        return "Second job executed";
    }

    @GetMapping("/third")
    public String thirdApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("thirdJob"), jobParameters);

        return "third job executed";
    }

    @GetMapping("/oldUsersBatch")
    public String oldUsersBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("oldUsersBatchJob"), jobParameters);

        return "Old users batch job executed";
    }

    @GetMapping("/oldUsersAlertBatch")
    public String oldUsersAlertBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("oldUsersAlertBatchJob"), jobParameters);

        return "Old users alert batch job executed";
    }

    @GetMapping("/oldPerformancesBatch")
    public String oldPerformancesBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("oldPerformancesBatchJob"), jobParameters);

        return "Old performances batch job executed";
    }

    @GetMapping("/timeoutReservationBatch")
    public String timeoutReservationBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("timeoutReservationBatchJob"), jobParameters);

        return "Timeout reservation batch job executed";
    }

    @GetMapping("/upcomingReservationAlertBatch")
    public String upcomingReservationAlertBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("upcomingReservationAlertBatchJob"), jobParameters);
        return "Upcoming reservation alert batch job executed";
    }

    @GetMapping("/reservationReviewAlertBatch")
    public String reservationReviewAlertBatchApi() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", now.toString())
                .toJobParameters();
        jobLauncher.run(jobRegistry.getJob("reservationReviewAlertBatchJob"), jobParameters);
        return "Reservation review alert batch job executed";
    }

    @GetMapping("/")
    public void test(){
        // 사용자
        IntStream.range(10000, 50000)
                .forEach(
                        i -> {
                            User user =
                                    new User(
                                            "user" + i + "@example.com",
                                            "User" + i,
                                            "abc123?!",
                                            UserRole.ROLE_USER);
                            userRepository.save(user);
                        });

    }
}
