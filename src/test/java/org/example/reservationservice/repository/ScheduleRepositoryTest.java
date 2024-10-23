package org.example.reservationservice.repository;

import org.example.reservationservice.model.ScheduleSlot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository underTest;

    @Test
    void ExistsByTimeStart_ScheduleSetExists_True() {
        LocalDateTime timeStart = LocalDateTime.of(2024,9,8,15,0);
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .timeStart(timeStart)
                .isVisible(true)
                .build();
        underTest.save(scheduleSlot);

        Boolean actual = underTest.existsByTimeStart(timeStart);

        assertThat(actual).isTrue();
    }

    @Test
    void ExistsByTimeStart_ScheduleSetDoesntExist_False() {
        LocalDateTime timeStart = LocalDateTime.of(2024,9,8,15,0);

        Boolean actual = underTest.existsByTimeStart(timeStart);

        assertThat(actual).isFalse();
    }

    @Test
    void FindByTimeStart_ScheduleSetExists_ReturnCorrectScheduleSlot() {
        LocalDateTime timeStart = LocalDateTime.of(2024,9,8,15,0);
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .timeStart(timeStart)
                .isVisible(true)
                .build();
        underTest.save(scheduleSlot);

        Optional<ScheduleSlot> actual = underTest.findByTimeStart(timeStart);

        assertThat(actual.get()).isNotNull().isEqualTo(scheduleSlot);
    }

    @Test
    void DeleteAllByTimeStartBetween_DeleteTwoOfThreeScheduleSlots_ShouldDeleteTwoScheduleSlots() {
        LocalDateTime timeStart1 = LocalDateTime.of(2024,9,8,15,0);
        ScheduleSlot scheduleSlot1 = ScheduleSlot.builder()
                .timeStart(timeStart1)
                .isVisible(true)
                .build();
        LocalDateTime timeStart2 = LocalDateTime.of(2024,9,8,16,0);
        ScheduleSlot scheduleSlot2 = ScheduleSlot.builder()
                .timeStart(timeStart2)
                .isVisible(true)
                .build();
        LocalDateTime timeStart3 = LocalDateTime.of(2024,9,8,17,0);
        ScheduleSlot scheduleSlot3 = ScheduleSlot.builder()
                .timeStart(timeStart3)
                .isVisible(true)
                .build();
        underTest.save(scheduleSlot1);
        underTest.save(scheduleSlot2);
        underTest.save(scheduleSlot3);

        underTest.deleteAllByTimeStartBetween(timeStart1, timeStart2);
        List<ScheduleSlot> scheduleSlots = underTest.findAll();

        assertThat(scheduleSlots.size()).isEqualTo(1);
        assertThat(scheduleSlots).isEqualTo(List.of(scheduleSlot3));
    }

    @Test
    void FindScheduleSlotsByTimeStartBetween_FindOneScheduleSlotFrom_ShouldReturnCorrectScheduleSlot() {
        LocalDateTime timeStart = LocalDateTime.of(2024,9,8,15,0);
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .timeStart(timeStart)
                .isVisible(true)
                .build();
        underTest.save(scheduleSlot);
        LocalDateTime timeStart1 = LocalDateTime.of(2024,9,8,14,0);
        LocalDateTime timeStart2 = LocalDateTime.of(2024,9,8,16,0);

        List<ScheduleSlot> actual = underTest.findScheduleSlotsByTimeStartBetween(timeStart1, timeStart2);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).isEqualTo(List.of(scheduleSlot));
    }
}