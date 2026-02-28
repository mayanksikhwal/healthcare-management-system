package com.healthcare.notificationservice.service;

import com.healthcare.notificationservice.dto.AppointmentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAppointmentConfirmation(AppointmentMessage message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(message.getPatientEmail());
            email.setSubject("Appointment Confirmation - Healthcare System");
            email.setText(
                    "Dear Patient,\n\n" +
                            "Your appointment has been successfully booked!\n\n" +
                            "Appointment Details:\n" +
                            "Date & Time: " + message.getAppointmentDateTime() + "\n" +
                            "Doctor: " + message.getDoctorEmail() + "\n" +
                            "Reason: " + message.getReason() + "\n\n" +
                            "Please arrive 10 minutes early.\n\n" +
                            "Healthcare System"
            );
            mailSender.send(email);
            log.info("Email sent to: {}", message.getPatientEmail());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}