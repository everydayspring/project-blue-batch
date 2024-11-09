package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.modifiedAt <= :targetDate AND u.isDeleted = false")
    Page<User> findAllOldUser(@Param("targetDate") LocalDateTime targetDate, Pageable pageable);
}
