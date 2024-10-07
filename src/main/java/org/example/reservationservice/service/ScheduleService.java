package org.example.reservationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.reservationservice.config.exception.ScheduleException;
import org.example.reservationservice.model.ScheduleSlot;
import org.example.reservationservice.model.ScheduleSlotRequest;
import org.example.reservationservice.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleSlot saveSchedule(ScheduleSlotRequest request) {
        ScheduleSlot schedule = ScheduleSlot.builder()
                .timeStart(request.timeStart())
                .isVisible(true)
                .build();

        if(scheduleRepository.existsByTimeStart(schedule.getTimeStart())) {
            throw new ScheduleException("schedule slot already exists");
        }

        return scheduleRepository.save(schedule);
    }

    public List<ScheduleSlot> findAll() {
        return scheduleRepository.findAll();
    }

    public void deleteSchedule(Long id) {
        Optional<ScheduleSlot> scheduleOptional = scheduleRepository.findById(id);
        scheduleRepository.delete(scheduleOptional.orElseThrow(() -> new ScheduleException("wrong schedule slot id")));
    }
}
