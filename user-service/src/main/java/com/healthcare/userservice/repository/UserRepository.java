package com.healthcare.userservice.repository;

import com.healthcare.userservice.entity.User;
import com.healthcare.userservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}
