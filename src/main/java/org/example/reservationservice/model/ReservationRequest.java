package org.example.reservationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ReservationRequest(String firstName, String lastName, String email, String phoneNumber, @JsonFormat(pattern = "yyyy-MM-dd HH:mm")LocalDateTime timeStart) {
}
