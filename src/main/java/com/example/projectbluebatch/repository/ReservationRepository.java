package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.modifiedAt <= :targetDate")
    Page<Reservation> findAllOldReservation(@Param("targetDate") LocalDateTime targetDate, Pageable pageable);
}
