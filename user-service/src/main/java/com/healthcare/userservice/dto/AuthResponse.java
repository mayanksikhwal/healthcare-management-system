package com.healthcare.userservice.dto;

import com.healthcare.userservice.enums.Role;
import lombok.*;

@Data
@AllArgsConstructor
@Builder

public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private Role role;
    private String message;
}
