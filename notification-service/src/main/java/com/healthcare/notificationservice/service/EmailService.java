package com.healthcare.notificationservice.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.healthcare.notificationservice.dto.AppointmentMessage;  // ADD THIS IMPORT

@Service
@Slf4j
public class EmailService {

    @Value("${spring.sendgrid.api-key}")
    private String sendgridApiKey;

    public void sendAppointmentConfirmation(AppointmentMessage message) {  // FIXED SIGNATURE
        try {
            Email from = new Email("sikhwalmayank251@gmail.com");
            Email to = new Email(message.getPatientEmail());
            
            Content content = new Content("text/plain", 
                "Dear Patient,\n\n" +
                "Your appointment has been successfully booked!\n\n" +
                "Appointment Details:\n" +
                "Date & Time: " + message.getAppointmentDateTime() + "\n" +
                "Doctor: " + message.getDoctorEmail() + "\n" +
                "Reason: " + message.getReason() + "\n\n" +
                "Please arrive 10 minutes early.\n\n" +
                "Healthcare System"
            );
            
            Mail mail = new Mail(from, "Appointment Confirmation - Healthcare System", to, content);
            
            SendGrid sg = new SendGrid(sendgridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            log.info("✅ Email sent to: {} - Status: {}", message.getPatientEmail(), response.getStatusCode());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", message.getPatientEmail(), e.getMessage());
        }
    }
}
