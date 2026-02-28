package com.healthcare.appointmentservice.dto;

import com.healthcare.appointmentservice.enums.AppointmentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientEmail;
    private Long doctorId;
    private String doctorEmail;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}