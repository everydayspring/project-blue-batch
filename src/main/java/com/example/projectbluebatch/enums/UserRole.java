package com.example.projectbluebatch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    private final String userRole;
}