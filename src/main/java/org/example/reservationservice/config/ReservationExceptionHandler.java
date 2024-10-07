package org.example.reservationservice.config;

import org.example.reservationservice.config.exception.ReservationException;
import org.example.reservationservice.config.exception.ScheduleException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {
    @ExceptionHandler(ScheduleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String scheduleExceptionHandler(ScheduleException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ReservationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String reservationExceptionHandler(ReservationException ex) {
        return ex.getMessage();
    }
}
