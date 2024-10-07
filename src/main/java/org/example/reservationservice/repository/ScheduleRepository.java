package org.example.reservationservice.repository;


import org.example.reservationservice.model.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleSlot, Long> {

    boolean existsByTimeStart(LocalDateTime timeStart);

    Optional<ScheduleSlot> findByTimeStart(LocalDateTime timeStart);

    void deleteAllByTimeStartBetween(LocalDateTime date1, LocalDateTime date2);

    List<ScheduleSlot> findScheduleSlotsByTimeStartBetween(LocalDateTime date1, LocalDateTime date2);
}
