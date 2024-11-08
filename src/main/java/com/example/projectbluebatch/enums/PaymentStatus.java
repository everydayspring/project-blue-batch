package com.example.projectbluebatch.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY("인증전"),
    DONE("결제승인"),
    CANCELED("취소");

    private final String value;

    PaymentStatus(String value) {

        this.value = value;
    }
}
