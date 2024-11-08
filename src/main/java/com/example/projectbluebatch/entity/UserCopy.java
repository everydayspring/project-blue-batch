package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users_copy")
public class UserCopy extends BaseEntity {

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

    public UserCopy(String email, String name, String password, UserRole userRole) {

        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
    }

    public UserCopy(String email, String name, String password, UserRole userRole, Long kakaoId) {

        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
        this.kakaoId = kakaoId;
    }

    public void userDeleted() {

        this.isDeleted = true;
    }

    public void InsertKakaoId(Long kakaoId) {

        this.kakaoId = kakaoId;
    }

    public void copy(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
        this.userRole = user.getUserRole();
        this.kakaoId = user.getKakaoId();
        this.isDeleted = user.isDeleted();
    }
}
