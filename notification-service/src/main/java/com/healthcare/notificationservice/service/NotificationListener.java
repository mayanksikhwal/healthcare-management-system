package com.healthcare.notificationservice.service;

import com.healthcare.notificationservice.config.RabbitMQConfig;
import com.healthcare.notificationservice.dto.AppointmentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleAppointmentNotification(AppointmentMessage message) {
        log.info("Received appointment notification for: {}", message.getPatientEmail());
        emailService.sendAppointmentConfirmation(message);
    }
}