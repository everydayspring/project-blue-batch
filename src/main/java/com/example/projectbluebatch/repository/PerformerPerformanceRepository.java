package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.PerformerPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformerPerformanceRepository extends JpaRepository<PerformerPerformance, Long> {

    Page<PerformerPerformance> findByPerformanceIdIn(List<Long> performanceIds, Pageable pageable);

}
