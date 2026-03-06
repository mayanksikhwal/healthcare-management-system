package com.healthcare.appointmentservice.service;

import com.healthcare.appointmentservice.config.RabbitMQConfig;
import com.healthcare.appointmentservice.dto.*;
import com.healthcare.appointmentservice.entity.Appointment;
import com.healthcare.appointmentservice.enums.AppointmentStatus;
import com.healthcare.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .patientEmail(request.getPatientEmail())
                .doctorId(request.getDoctorId())
                .doctorEmail(request.getDoctorEmail())
                .appointmentDateTime(request.getAppointmentDateTime())
                .reason(request.getReason())
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

	RestTemplate restTemplate = new RestTemplate();  

	logger.info("🔍 DEBUG: Sending email to: {}", saved.getPatientEmail());
	logger.info("🔍 DEBUG: Doctor email: {}", saved.getDoctorEmail());
	logger.info("🔍 DEBUG: Appointment ID: {}", saved.getId());
	logger.info("🔍 DEBUG: Calling notification-service...");

        // Publish message to RabbitMQ
        /* AppointmentMessage message = new AppointmentMessage(
                saved.getId(),
                saved.getPatientEmail(),
                saved.getDoctorEmail(),
                saved.getAppointmentDateTime(),
                saved.getReason()
        ); 
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        ); */

	        // DIRECT EMAIL - No RabbitMQ needed
        try {
            AppointmentMessage message = new AppointmentMessage(  // 👈 Use existing DTO
    		saved.getId(),
    		saved.getPatientEmail(),
    		saved.getDoctorEmail(),
    		saved.getAppointmentDateTime(),
    		saved.getReason()
		);            
            restTemplate.postForObject(
    		"https://healthcare-management-system-3-cpcw.onrender.com/api/notifications/email",
    		message,
    		String.class
		);

        } catch (Exception e) {
    		logger.error("🚨 EMAIL ERROR: {} - {}", e.getClass().getSimpleName(), e.getMessage());  
		}


        // return mapToResponse(appointmentRepository.save(appointment));
	return mapToResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        return mapToResponse(appointmentRepository.save(appointment));
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatientId())
                .patientEmail(a.getPatientEmail())
                .doctorId(a.getDoctorId())
                .doctorEmail(a.getDoctorEmail())
		.doctorName(a.getDoctorEmail().split("@")[0])
                .appointmentDateTime(a.getAppointmentDateTime())
                .reason(a.getReason())
                .notes(a.getNotes())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}