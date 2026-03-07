package com.healthcare.appointmentservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentMessage {
    private Long appointmentId;
    private String patientEmail;
    private String doctorEmail;
    private LocalDateTime appointmentDateTime; 
    private String reason;
}