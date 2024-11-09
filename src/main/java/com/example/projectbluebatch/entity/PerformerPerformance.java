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
@Table(name = "performer_performance")
public class PerformerPerformance extends BaseEntity {
    @Column(nullable = false, name = "performer_id")
    private Long performerId;

    @Column(nullable = false, name = "performance_id")
    private Long performanceId;
}
