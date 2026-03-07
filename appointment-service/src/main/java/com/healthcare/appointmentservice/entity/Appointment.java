package com.healthcare.appointmentservice.entity;

import com.healthcare.appointmentservice.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private String patientEmail;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private String doctorEmail;

    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    private String reason;
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = AppointmentStatus.PENDING;
    }
}