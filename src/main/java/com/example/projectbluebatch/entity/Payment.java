package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, name = "reservation_id")
    private Long reservationId;

    @Column(nullable = false, name = "performance_id")
    private Long performanceId;

    @Column(length = 255, name = "payment_key")
    private String paymentKey;

    @Column(length = 20)
    private String type; // 결제 종류 : NORMAL, BILLING, BRANDPAY

    @Column(length = 20)
    private String method; // 결제 수단 : 카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 문화상품권, 도서상품권, 게임문화상품권

    @Column(nullable = false, name = "origin_amount")
    private Long originAmount; // 실제 결제한 가격

    @Column(name = "amount_supplied")
    private Long amountSupplied; // 결제 수수료의 공급 가액

    @Column(name = "amount_vat")
    private Long amountVat; // 수수료

    @Column(name = "amount_total")
    private Long amountTotal; // suppliedAmount + vat, 원래 가격

    @Column(name = "discount_value")
    private Long discountValue; // 쿠폰 할인 금액

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(nullable = false, length = 30, name = "order_id")
    private String orderId;

    public void canceled() {
        this.status = PaymentStatus.CANCELED;
    }
}
