package com.example.projectbluebatch.enums;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("결제대기"),
    COMPLETED("예매완료"),
    CANCELED("예매취소");

    private final String status;

    ReservationStatus(String status) {

        this.status = status;
    }
}
