package com.example.projectbluebatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpcomingReservationAlertInfo {

    private Long userId;
    private String userName;
    private String slackId;
    private Long performanceId;
    private String performanceTitle;
    private Long hallId;
    private LocalDateTime date;
    private String hallName;
}