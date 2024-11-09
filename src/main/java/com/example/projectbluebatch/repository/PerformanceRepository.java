package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("SELECT r FROM Performance r WHERE r.endDate <= :targetDate")
    Page<Performance> findAllOldPerformance(@Param("targetDate") LocalDateTime targetDate, Pageable pageable);
}
