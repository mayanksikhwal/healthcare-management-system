package com.healthcare.userservice.controller;

import com.healthcare.userservice.entity.User;
import com.healthcare.userservice.enums.Role;
import com.healthcare.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors() {
        List<User> doctors = userRepository.findByRole(Role.DOCTOR);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
