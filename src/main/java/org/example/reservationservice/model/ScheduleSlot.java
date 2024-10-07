package org.example.reservationservice.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "schedules")
public class ScheduleSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    @Column(name = "schedule_time_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeStart;
    @Column(name = "schedule_is_visible")
    private boolean isVisible;
}
