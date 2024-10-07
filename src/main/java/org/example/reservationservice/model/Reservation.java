package org.example.reservationservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="reservations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;
    @Column(name = "reservation_first_name")
    private String firstName;
    @Column(name = "reservation_last_name")
    private String lastName;
    @Column(name = "reservation_email")
    private String email;
    @Column(name = "reservation_phone_number")
    private String phoneNumber;
    @Column(name = "reservation_status")
    private ReservationStatus status;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "reservation_schedule_slot")
    private ScheduleSlot scheduleSlot;
    private Integer durationInMinutes;
}
