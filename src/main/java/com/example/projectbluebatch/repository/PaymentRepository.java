package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT r FROM Payment r WHERE r.modifiedAt <= :targetDate")
    Page<Payment> findAllOldPayment(@Param("targetDate") LocalDateTime targetDate, Pageable pageable);
}
