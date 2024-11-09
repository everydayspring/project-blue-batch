package com.example.projectbluebatch.enums;

import lombok.Getter;

@Getter
public enum ReviewRate {
    ZERO("0점"),
    ONE("1점"),
    TWO("2점"),
    THREE("3점"),
    FOUR("4점"),
    FIVE("5점");

    private final String value;

    ReviewRate(String value) {

        this.value = value;
    }
}
