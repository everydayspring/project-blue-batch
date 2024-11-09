package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Round;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {

    Page<Round> findByPerformanceIdIn(List<Long> performanceIds, Pageable pageable);

}
