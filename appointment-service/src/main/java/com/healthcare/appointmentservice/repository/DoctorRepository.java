package com.healthcare.appointmentservice.repository;

import com.healthcare.appointmentservice.entity.Doctor;  // DO YOU HAVE THIS?
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);  // REQUIRED METHOD
}
