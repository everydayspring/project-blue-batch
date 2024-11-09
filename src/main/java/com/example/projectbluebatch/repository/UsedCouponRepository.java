package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.UsedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedCouponRepository extends JpaRepository<UsedCoupon, Long> {

    Page<UsedCoupon> findByReservationIdIn(List<Long> reservationIds, Pageable pageable);

}
