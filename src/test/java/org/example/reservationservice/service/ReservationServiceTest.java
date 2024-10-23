package org.example.reservationservice.service;

import org.example.reservationservice.config.exception.ReservationException;
import org.example.reservationservice.config.exception.ScheduleException;
import org.example.reservationservice.model.Reservation;
import org.example.reservationservice.model.ReservationRequest;
import org.example.reservationservice.model.ReservationStatus;
import org.example.reservationservice.model.ScheduleSlot;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    private ReservationService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ReservationService(reservationRepository, scheduleRepository);
    }

    @Test
    void FindAllByStatus_InvokedWithCorrectStatus_ShouldReturnReservationsWithCorrectStatus() {
        Reservation reservationAccepted =
                Reservation.builder().status(ReservationStatus.ACCEPTED).build();
        Reservation reservationPending =
                Reservation.builder().status(ReservationStatus.PENDING).build();
        given(reservationRepository.findAll()).willReturn(List.of(reservationAccepted, reservationPending));

        List<Reservation> actual = underTest.findAllByStatus("ACCEPTED");

        assertThat(actual).hasSize(1).containsExactly(reservationAccepted);
    }

    @Test
    void FindAllByStatus_InvokedWithIncorrectStatus_ShouldThrowReservationException() {
        assertThatThrownBy(() -> underTest.findAllByStatus("wrongStatus"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("wrong status");
        verify(reservationRepository, never()).findAll();
    }

    @Test
    void FindAll_WhenInvoked_ShouldCallFindAllInRepo() {
        underTest.findAll();

        verify(reservationRepository).findAll();
    }

    @Test
    void SaveReservation_CorrectRequest_ShouldCallSaveInRepoAndReturnReservation() {
        LocalDateTime date = LocalDateTime.of(2024, 10,8,20,0);
        ReservationRequest request = new ReservationRequest(
                "firstName",
                "lastName",
                "email",
                "123123123",
                date);
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(true)
                .timeStart(date)
                .build();
        given(scheduleRepository.findByTimeStart(date)).willReturn(Optional.of(scheduleSlot));


        underTest.saveReservation(request);


        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationArgumentCaptor.capture());
        Reservation actualReservation = reservationArgumentCaptor.getValue();

        assertThat(actualReservation.getScheduleSlot()).isEqualTo(scheduleSlot);
        assertThat(actualReservation.getFirstName()).isEqualTo(request.firstName());
        assertThat(actualReservation.getLastName()).isEqualTo(request.lastName());
        assertThat(actualReservation.getPhoneNumber()).isEqualTo(request.phoneNumber());
        assertThat(actualReservation.getEmail()).isEqualTo(request.email());
        assertThat(actualReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void SaveReservation_ScheduleSlotIsEmpty_ShouldThrowScheduleException() {
        ReservationRequest request = new ReservationRequest(
                "firstName",
                "lastName",
                "email",
                "123123123",
                LocalDateTime.of(2024, 10,8,20,0));
        given(scheduleRepository.findByTimeStart(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.saveReservation(request))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("incorrect schedule slot");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void SaveReservation_ScheduleSlotIsNotVisible_ShouldThrowScheduleException() {
        LocalDateTime date = LocalDateTime.of(2024, 10,8,20,0);
        ReservationRequest request = new ReservationRequest(
                "firstName",
                "lastName",
                "email",
                "123123123",
                date);
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(false)
                .timeStart(date)
                .build();
        given(scheduleRepository.findByTimeStart(any())).willReturn(Optional.of(scheduleSlot));

        assertThatThrownBy(() -> underTest.saveReservation(request))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("incorrect schedule slot");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void AcceptReservation_CorrectId_ShouldAcceptReservation() {
        Integer duration = 60;
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(true)
                .timeStart(LocalDateTime.of(2024, 10,8,20,0))
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.PENDING)
                .scheduleSlot(scheduleSlot)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("123123123")
                .build();
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));
        given(scheduleRepository.findScheduleSlotsByTimeStartBetween(any(), any())).willReturn(anyList());

        underTest.acceptReservation(1L, duration);

        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        Reservation actualReservation = reservationArgumentCaptor.getValue();

        assertThat(actualReservation.getStatus()).isEqualTo(ReservationStatus.ACCEPTED);
        assertThat(actualReservation.getDurationInMinutes()).isEqualTo(duration);
        assertThat(scheduleSlot.isVisible()).isFalse();
        assertThat(actualReservation.getFirstName()).isEqualTo(reservation.getFirstName());
        assertThat(actualReservation.getLastName()).isEqualTo(reservation.getLastName());
        assertThat(actualReservation.getEmail()).isEqualTo(reservation.getEmail());
        assertThat(actualReservation.getPhoneNumber()).isEqualTo(reservation.getPhoneNumber());
    }

    @Test
    void AcceptReservation_CorrectIdAndWrongStatus_ShouldThrowReservationException() {
        Integer duration = 60;
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(true)
                .timeStart(LocalDateTime.of(2024, 10,8,20,0))
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.ACCEPTED)
                .scheduleSlot(scheduleSlot)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("123123123")
                .build();
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> underTest.acceptReservation(1L, duration))
                .isInstanceOf(ReservationException.class)
                .hasMessage("this reservation is already accepted");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void AcceptReservation_CorrectIdAndConflicts_ShouldThrowReservationException() {
        Integer duration = 60;
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(true)
                .timeStart(LocalDateTime.of(2024, 10,8,20,0))
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.PENDING)
                .scheduleSlot(scheduleSlot)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("123123123")
                .build();
        ScheduleSlot scheduleSlotConflicted = ScheduleSlot.builder()
                .isVisible(false)
                .timeStart(LocalDateTime.of(2024, 10,8,20,30))
                .build();
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));
        given(scheduleRepository.findScheduleSlotsByTimeStartBetween(any(), any())).willReturn(List.of(scheduleSlotConflicted));

        assertThatThrownBy(() -> underTest.acceptReservation(1L, duration))
                .isInstanceOf(ReservationException.class)
                .hasMessage("reservation conflicts with other reservation");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void RejectReservation_CorrectId_ShouldRejectReservation() {
        ScheduleSlot scheduleSlot = ScheduleSlot.builder()
                .isVisible(true)
                .timeStart(LocalDateTime.of(2024, 10,8,20,0))
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.PENDING)
                .scheduleSlot(scheduleSlot)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("123123123")
                .build();
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        underTest.rejectReservation(anyLong());

        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        Reservation actualReservation = reservationArgumentCaptor.getValue();

        assertThat(actualReservation.getStatus()).isEqualTo(ReservationStatus.REJECTED);
    }

    @Test
    void RejectReservation_CorrectIdButWrongStatus_ShouldThrowReservationException() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.ACCEPTED)
                .scheduleSlot(null)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("123123123")
                .build();
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> underTest.rejectReservation(anyLong()))
                .isInstanceOf(ReservationException.class)
                .hasMessage("this reservation is already accepted");

        verify(reservationRepository, never()).save(any());
    }
}