package org.example.reservationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.reservationservice.config.exception.ReservationException;
import org.example.reservationservice.config.exception.ScheduleException;
import org.example.reservationservice.model.Reservation;
import org.example.reservationservice.model.ReservationRequest;
import org.example.reservationservice.model.ReservationStatus;
import org.example.reservationservice.model.ScheduleSlot;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;

    public List<Reservation> findAllByStatus(String status) {
        try {
            return reservationRepository.findAll()
                    .stream()
                    .filter(reservation -> reservation.getStatus() == ReservationStatus.valueOf(status))
                    .toList();
        } catch(IllegalArgumentException ex) {
            throw new ReservationException("wrong status");
        }
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation saveReservation(ReservationRequest request) {
        Optional<ScheduleSlot> scheduleSlot = scheduleRepository.findByTimeStart(request.timeStart());
        if(scheduleSlot.isEmpty() || !scheduleSlot.get().isVisible()) {
            throw new ScheduleException("incorrect schedule slot");
        }

        Reservation reservation = Reservation.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .status(ReservationStatus.PENDING)
                .scheduleSlot(scheduleSlot.get())
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation acceptReservation(Long id, Integer duration) {
        Reservation reservation = returnReservationIfExistsOrException(id);

        if(!(reservation.getStatus() == ReservationStatus.PENDING)) {
            throw new ReservationException("this reservation is already accepted");
        }

        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setDurationInMinutes(duration);

        LocalDateTime timeEnd = reservation.getScheduleSlot().getTimeStart().plusMinutes(duration);
        if(scheduleRepository.findScheduleSlotsByTimeStartBetween(reservation.getScheduleSlot().getTimeStart().plusMinutes(1), timeEnd.minusMinutes(1))
                .stream().anyMatch(scheduleSet -> !scheduleSet.isVisible())) {
            throw new ReservationException("reservation conflicts with other reservation");
        }
        scheduleRepository.deleteAllByTimeStartBetween(reservation.getScheduleSlot().getTimeStart().plusMinutes(1), timeEnd.minusMinutes(1));
        reservation.getScheduleSlot().setVisible(false);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation rejectReservation(Long id) {
        Reservation reservation = returnReservationIfExistsOrException(id);

        if(!(reservation.getStatus() == ReservationStatus.PENDING)) {
            throw new ReservationException("this reservation is already accepted");
        }

        reservation.setStatus(ReservationStatus.REJECTED);

        return reservation;
    }
    private Reservation returnReservationIfExistsOrException(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationException("reservation not found"));
    }
}
