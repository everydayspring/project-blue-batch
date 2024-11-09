package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByReservationIdIn(List<Long> reservationIds, Pageable pageable);
}
