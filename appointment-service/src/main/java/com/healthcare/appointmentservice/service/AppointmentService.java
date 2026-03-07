package com.healthcare.appointmentservice.service;

import com.healthcare.appointmentservice.config.RabbitMQConfig;
import com.healthcare.appointmentservice.dto.*;
import com.healthcare.appointmentservice.entity.Appointment;
import com.healthcare.appointmentservice.enums.AppointmentStatus;
import com.healthcare.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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
		.doctorName(request.getDoctorName())
                .appointmentDateTime(request.getAppointmentDateTime())
                .reason(request.getReason())
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // Publish message to RabbitMQ
        try {
            AppointmentMessage message = new AppointmentMessage(
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
            );
            logger.info("Message published to RabbitMQ for appointment: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Failed to publish RabbitMQ message: {}", e.getMessage());
        }

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
                .doctorName(a.getDoctorName())
                .appointmentDateTime(a.getAppointmentDateTime())
                .reason(a.getReason())
                .notes(a.getNotes())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
