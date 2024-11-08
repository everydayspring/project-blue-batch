package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.createdAt <= :threeYearsAgo")
    Slice<User> findAllCreatedBefore(@Param("threeYearsAgo") LocalDateTime threeYearsAgo, Pageable pageable);
}
