package org.example.reservationservice.rest;

import lombok.RequiredArgsConstructor;
import org.example.reservationservice.model.ScheduleSlot;
import org.example.reservationservice.model.ScheduleSlotRequest;
import org.example.reservationservice.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleSlot> saveSchedule(@RequestBody ScheduleSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.saveSchedule(request));
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleSlot>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.findAll());
    }

    @DeleteMapping("/schedules/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }
}
