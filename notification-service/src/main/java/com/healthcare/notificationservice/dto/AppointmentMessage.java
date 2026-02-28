package com.healthcare.notificationservice.dto;

import lombok.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentMessage {
    private Long appointmentId;
    private String patientEmail;
    private String doctorEmail;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime appointmentDateTime;

    private String reason;
}