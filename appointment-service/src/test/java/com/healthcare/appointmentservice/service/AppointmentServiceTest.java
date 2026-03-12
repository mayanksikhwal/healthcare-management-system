package com.healthcare.appointmentservice.service;

import com.healthcare.appointmentservice.dto.AppointmentRequest;
import com.healthcare.appointmentservice.dto.AppointmentResponse;
import com.healthcare.appointmentservice.entity.Appointment;
import com.healthcare.appointmentservice.enums.AppointmentStatus;
import com.healthcare.appointmentservice.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentRequest request;
    private Appointment savedAppointment;

    @BeforeEach
    void setUp() {
        request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setPatientEmail("patient@gmail.com");
        request.setDoctorId(2L);
        request.setDoctorEmail("doctor@gmail.com");
        request.setDoctorName("Dr. Rohan");
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        request.setReason("Regular checkup");
        request.setNotes("First visit");

        savedAppointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .patientEmail("patient@gmail.com")
                .doctorId(2L)
                .doctorEmail("doctor@gmail.com")
                .doctorName("Dr. Rohan")
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .reason("Regular checkup")
                .notes("First visit")
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // createAppointment TESTS 

    @Test
    void whenValidRequest_createAppointment_shouldReturnPendingStatus() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        assertEquals("patient@gmail.com", response.getPatientEmail());
        assertEquals("Dr. Rohan", response.getDoctorName());
    }

    @Test
    void whenValidRequest_createAppointment_shouldSaveToRepository() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.createAppointment(request);

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void whenValidRequest_createAppointment_shouldPublishToRabbitMQ() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.createAppointment(request);

        verify(rabbitTemplate, times(1)).convertAndSend(
                anyString(), anyString(), any(Object.class)
        );
    }

    @Test
    void whenRabbitMQFails_createAppointment_shouldStillSaveAppointment() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    // getAppointmentById TESTS 

    @Test
    void whenValidId_getAppointmentById_shouldReturnAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));

        AppointmentResponse response = appointmentService.getAppointmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("patient@gmail.com", response.getPatientEmail());
    }

    @Test
    void whenInvalidId_getAppointmentById_shouldThrowException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.getAppointmentById(99L)
        );
        assertEquals("Appointment not found with id: 99", exception.getMessage());
    }

    // getAppointmentsByPatient TESTS 

    @Test
    void whenValidPatientId_getAppointmentsByPatient_shouldReturnList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(savedAppointment));

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByPatient(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("patient@gmail.com", responses.get(0).getPatientEmail());
    }

    @Test
    void whenPatientHasNoAppointments_getAppointmentsByPatient_shouldReturnEmptyList() {
        when(appointmentRepository.findByPatientId(99L)).thenReturn(List.of());

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByPatient(99L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // getAppointmentsByDoctor TESTS 

    @Test
    void whenValidDoctorId_getAppointmentsByDoctor_shouldReturnList() {
        when(appointmentRepository.findByDoctorId(2L)).thenReturn(List.of(savedAppointment));

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByDoctor(2L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("doctor@gmail.com", responses.get(0).getDoctorEmail());
    }

    // updateStatus TESTS 

    @Test
    void whenValidId_updateStatus_shouldReturnUpdatedStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponse response = appointmentService.updateStatus(1L, AppointmentStatus.CONFIRMED);

        assertNotNull(response);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void whenInvalidId_updateStatus_shouldThrowException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> appointmentService.updateStatus(99L, AppointmentStatus.CONFIRMED)
        );
        assertEquals("Appointment not found with id: 99", exception.getMessage());
    }

    // cancelAppointment TESTS 

    @Test
    void whenValidId_cancelAppointment_shouldSetStatusCancelled() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(savedAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.cancelAppointment(1L);

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        assertEquals(AppointmentStatus.CANCELLED, savedAppointment.getStatus());
    }

    @Test
    void whenInvalidId_cancelAppointment_shouldThrowException() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> appointmentService.cancelAppointment(99L)
        );
    }

    // getAllAppointments TESTS 

    @Test
    void getAllAppointments_shouldReturnAllAppointments() {
        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .patientId(3L)
                .patientEmail("patient2@gmail.com")
                .doctorId(2L)
                .doctorEmail("doctor@gmail.com")
                .doctorName("Dr. Rohan")
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .reason("Follow up")
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(appointmentRepository.findAll()).thenReturn(List.of(savedAppointment, appointment2));

        List<AppointmentResponse> responses = appointmentService.getAllAppointments();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }
}
