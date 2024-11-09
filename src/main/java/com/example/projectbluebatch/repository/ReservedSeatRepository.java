package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.ReservedSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {

    Page<ReservedSeat> findByReservationIdIn(List<Long> reservationIds, Pageable pageable);

}
