package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "user_role")
    private UserRole userRole;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "salck_id")
    private Long slackId;

    public void userDeleted() {

        this.isDeleted = true;
    }
}