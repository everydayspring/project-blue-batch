package com.example.projectbluebatch.entity;

import com.example.projectbluebatch.entity.base.BaseEntity;
import com.example.projectbluebatch.enums.ReviewRate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Column(nullable = false, name = "performance_id")
    private Long performanceId;

    @Column(nullable = false, name = "reservation_id")
    private Long reservationId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 5, name = "review_rate")
    private ReviewRate reviewRate;

    @Column(nullable = false, length = 255)
    private String content;
}
