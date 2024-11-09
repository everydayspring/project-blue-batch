package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.Poster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosterRepository extends JpaRepository<Poster, Long> {

    Page<Poster> findByPerformanceIdIn(List<Long> performanceIds, Pageable pageable);

}
