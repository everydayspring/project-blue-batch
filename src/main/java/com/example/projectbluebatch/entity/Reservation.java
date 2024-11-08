package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = true, name = "payment_id")
    private Long paymentId;

    @Column(nullable = false, name = "performance_id")
    private Long performanceId;

    @Column(nullable = false, name = "round_id")
    private Long roundId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReservationStatus status;

    @Column(nullable = false)
    private Long price;
}