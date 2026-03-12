package com.healthcare.notificationservice.service;

import com.healthcare.notificationservice.dto.AppointmentMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    private AppointmentMessage message;

    @BeforeEach
    void setUp() {
        // Inject fake SendGrid API key using ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "sendgridApiKey", "fake-test-api-key");

        message = new AppointmentMessage(
                1L,
                "patient@gmail.com",
                "doctor@gmail.com",
                LocalDateTime.now().plusDays(1),
                "Regular checkup"
        );
    }

    @Test
    void whenSendGridFails_sendAppointmentConfirmation_shouldNotThrowException() {
        assertDoesNotThrow(() -> emailService.sendAppointmentConfirmation(message));
    }

    @Test
    void whenNullPatientEmail_sendAppointmentConfirmation_shouldNotThrowException() {
        message.setPatientEmail(null);

        assertDoesNotThrow(() -> emailService.sendAppointmentConfirmation(message));
    }

    @Test
    void whenValidMessage_sendAppointmentConfirmation_shouldHandleApiFailureGracefully() {
        assertDoesNotThrow(() -> emailService.sendAppointmentConfirmation(message));
    }
}
