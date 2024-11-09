package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByUserIdIn(List<Long> userIds, Pageable pageable);

    Page<Reservation> findByPerformanceIdIn(List<Long> performanceIds, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE FUNCTION('DATE', r.modifiedAt) = :targetDate AND r.paymentId IS NULL AND r.status = 'PENDING'")
    Page<Reservation> findAllTimeoutReservations(@Param("targetDate") LocalDate targetDate, Pageable pageable);
}
