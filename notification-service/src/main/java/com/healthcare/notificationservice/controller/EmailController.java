package com.healthcare.notificationservice.controller;

import com.healthcare.notificationservice.dto.AppointmentMessage;
import com.healthcare.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendAppointmentEmail(@RequestBody AppointmentMessage message) {
        emailService.sendAppointmentConfirmation(message);
        return ResponseEntity.ok("Email sent to " + message.getPatientEmail());
    }
}
