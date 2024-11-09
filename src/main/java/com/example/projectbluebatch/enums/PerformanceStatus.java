package com.example.projectbluebatch.enums;

import lombok.Getter;

@Getter
public enum PerformanceStatus {
    BEFORE_OPEN("예매오픈전"),
    AVAILABLE("예매가능"),
    SOLD_OUT("매진");

    private final String status;

    PerformanceStatus(String status) {

        this.status = status;
    }
}
