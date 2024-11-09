package com.example.projectbluebatch.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
    CONCERT("콘서트"),
    MUSICAL("뮤지컬"),
    SPORTS("스포츠");

    private final String value;

    public static Category of(String category) {

        return Arrays.stream(Category.values())
                .filter(r -> r.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Category"));
    }

    Category(String value) {

        this.value = value;
    }
}
