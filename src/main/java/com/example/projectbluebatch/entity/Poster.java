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
@Table(name = "posters")
public class Poster extends BaseEntity {

    @Column(nullable = false, name = "performance_id")
    private Long performanceId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 255, name = "image_url")
    private String imageUrl;

    @Column(nullable = false, name = "file_size")
    private Long fileSize;
}
