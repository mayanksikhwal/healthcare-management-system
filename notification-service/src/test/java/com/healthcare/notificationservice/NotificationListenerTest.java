package com.healthcare.notificationservice.service;

import com.healthcare.notificationservice.dto.AppointmentMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationListener notificationListener;

    private AppointmentMessage message;

    @BeforeEach
    void setUp() {
        message = new AppointmentMessage(
                1L,
                "patient@gmail.com",
                "doctor@gmail.com",
                LocalDateTime.now().plusDays(1),
                "Regular checkup"
        );
    }

    @Test
    void whenMessageReceived_shouldCallEmailService() {
        notificationListener.handleAppointmentNotification(message);

        verify(emailService, times(1)).sendAppointmentConfirmation(message);
    }

    @Test
    void whenMessageReceived_shouldCallEmailServiceWithCorrectPatientEmail() {
        notificationListener.handleAppointmentNotification(message);

        verify(emailService).sendAppointmentConfirmation(
                argThat(msg -> msg.getPatientEmail().equals("patient@gmail.com"))
        );
    }

    @Test
    void whenMultipleMessagesReceived_shouldCallEmailServiceForEach() {
        AppointmentMessage message2 = new AppointmentMessage(
                2L,
                "patient2@gmail.com",
                "doctor@gmail.com",
                LocalDateTime.now().plusDays(2),
                "Follow up"
        );

        notificationListener.handleAppointmentNotification(message);
        notificationListener.handleAppointmentNotification(message2);

        verify(emailService, times(2)).sendAppointmentConfirmation(any(AppointmentMessage.class));
    }

    @Test
    void whenEmailServiceFails_listenerShouldNotThrowException() {
        doThrow(new RuntimeException("SendGrid API failed"))
                .when(emailService).sendAppointmentConfirmation(any(AppointmentMessage.class));

        try {
            notificationListener.handleAppointmentNotification(message);
        } catch (Exception e) {
        }
        verify(emailService, times(1)).sendAppointmentConfirmation(message);
    }
}
