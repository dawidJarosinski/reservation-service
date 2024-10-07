package org.example.reservationservice.rest;


import lombok.RequiredArgsConstructor;
import org.example.reservationservice.model.Reservation;
import org.example.reservationservice.model.ReservationRequest;
import org.example.reservationservice.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> findReservationsByStatus(@RequestParam(required = false) String status){
        if(status == null) {
            return ResponseEntity.ok(reservationService.findAll());
        }
        return ResponseEntity.ok(reservationService.findAllByStatus(status));
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> saveReservation(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.saveReservation(request));
    }

    @PatchMapping("/reservations/{id}/accept")
    public ResponseEntity<Reservation> acceptReservation(@PathVariable Long id, @RequestParam Integer duration) {
        return ResponseEntity.ok(reservationService.acceptReservation(id, duration));
    }

    @PatchMapping("/reservations/{id}/reject")
    public ResponseEntity<Reservation> rejectReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.rejectReservation(id));
    }
}
