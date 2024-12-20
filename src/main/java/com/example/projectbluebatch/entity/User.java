package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
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

    @Column(name = "slack_id")
    private String slackId;

    public void userDeleted() {

        this.isDeleted = true;
    }

    public User(String email, String name, String password, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
    }
}