package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByUserIdIn(List<Long> userIds, Pageable pageable);

    Page<Reservation> findByPerformanceIdIn(List<Long> performanceIds, Pageable pageable);

}
