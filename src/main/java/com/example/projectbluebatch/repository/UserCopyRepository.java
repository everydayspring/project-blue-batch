package com.example.projectbluebatch.repository;

import com.example.projectbluebatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCopyRepository extends JpaRepository<User, Long> {
}
