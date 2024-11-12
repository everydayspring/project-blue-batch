package com.example.projectbluebatch.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    READY("인증전"),
    DONE("결제승인"),
    CANCELED("취소");

    private final String value;
}
