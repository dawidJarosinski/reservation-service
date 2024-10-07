package org.example.reservationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ScheduleSlotRequest(@JsonFormat(pattern = "yyyy-MM-dd HH:mm")LocalDateTime timeStart) {
}
