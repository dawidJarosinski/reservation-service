package org.example.reservationservice.service;

import org.example.reservationservice.config.exception.ScheduleException;
import org.example.reservationservice.model.ScheduleSlot;
import org.example.reservationservice.model.ScheduleSlotRequest;
import org.example.reservationservice.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    private ScheduleService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ScheduleService(scheduleRepository);
    }

    @Test
    void SaveSchedule_TimeStartIsNotTaken_ShouldReturnAndCallSaveWithCorrectScheduleSlot() {
        ScheduleSlotRequest request =
                new ScheduleSlotRequest(LocalDateTime.of(2024, 10,8,20,0));

        when(scheduleRepository.existsByTimeStart(any())).thenReturn(false);
        underTest.saveSchedule(request);

        ArgumentCaptor<ScheduleSlot> scheduleSlotArgumentCaptor = ArgumentCaptor.forClass(ScheduleSlot.class);
        verify(scheduleRepository).save(scheduleSlotArgumentCaptor.capture());

        assertThat(scheduleSlotArgumentCaptor.getValue().getTimeStart()).isEqualTo(request.timeStart());
        assertThat(scheduleSlotArgumentCaptor.getValue().isVisible()).isEqualTo(true);
    }

    @Test
    void SaveSchedule_TimeStartIsTaken_ShouldThrowScheduleException() {
        ScheduleSlotRequest request =
                new ScheduleSlotRequest(LocalDateTime.of(2024, 10,8,20,0));
        given(scheduleRepository.existsByTimeStart(any())).willReturn(true);

        assertThatThrownBy(() -> underTest.saveSchedule(request))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("schedule slot already exists");
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void FindAll_WhenInvoked_ShouldCallFindAllInRepository() {
        underTest.findAll();

        verify(scheduleRepository).findAll();
    }

    @Test
    void DeleteSchedule_IdIsCorrect_ShouldDeleteScheduleSlot() {
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .id(1L)
                .timeStart(LocalDateTime.of(2024, 10,8,20,0))
                .isVisible(true)
                .build();
        given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(scheduleSlot));

        underTest.deleteSchedule(1L);

        ArgumentCaptor<ScheduleSlot> scheduleSlotArgumentCaptor = ArgumentCaptor.forClass(ScheduleSlot.class);
        verify(scheduleRepository).delete(scheduleSlotArgumentCaptor.capture());
        assertThat(scheduleSlotArgumentCaptor.getValue()).isEqualTo(scheduleSlot);
    }

    @Test
    void DeleteSchedule_IdIsIncorrect_ShouldThrowScheduleException() {
        given(scheduleRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteSchedule(anyLong()))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("wrong schedule slot id");
        verify(scheduleRepository, never()).delete(any());
    }
}