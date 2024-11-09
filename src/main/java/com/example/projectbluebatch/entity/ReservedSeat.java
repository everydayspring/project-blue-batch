package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reserved_seats")
public class ReservedSeat extends BaseEntity {

    @Column(nullable = false, name = "reservation_id")
    private Long reservationId;

    @Column(nullable = false, name = "round_id")
    private Long roundId;

    @Column(nullable = false, name = "seat_number")
    private int seatNumber;
}
