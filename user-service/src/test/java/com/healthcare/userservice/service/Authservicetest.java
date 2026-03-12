package com.healthcare.userservice.service;

import com.healthcare.userservice.dto.AuthResponse;
import com.healthcare.userservice.dto.LoginRequest;
import com.healthcare.userservice.dto.RegisterRequest;
import com.healthcare.userservice.entity.User;
import com.healthcare.userservice.enums.Role;
import com.healthcare.userservice.exception.UserAlreadyExistsException;
import com.healthcare.userservice.repository.UserRepository;
import com.healthcare.userservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // MOCKS 
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    // object with mocks injected
    @InjectMocks
    private AuthService authService;

    // Test data
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Mayank");
        registerRequest.setEmail("mayank@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.PATIENT);
        registerRequest.setPhone("9908728426");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("mayank@gmail.com");
        loginRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .name("Mayank")
                .email("mayank@gmail.com")
                .password("encodedPassword123")
                .role(Role.PATIENT)
                .phone("9908728426")
                .build();
    }

    // register TESTS 

    @Test
    void whenValidRequest_register_shouldReturnAuthResponse() {
        when(userRepository.existsByEmail("mayank@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Mayank", response.getName());
        assertEquals("mayank@gmail.com", response.getEmail());
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("Registration successful!", response.getMessage());
        assertEquals(Role.PATIENT, response.getRole());
    }

    @Test
    void whenValidRequest_register_shouldEncodePassword() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void whenValidRequest_register_shouldSaveUserToRepository() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        authService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenValidRequest_register_shouldGenerateJwtToken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
        assertNotNull(response.getToken());
    }

    @Test
    void whenEmailAlreadyExists_register_shouldThrowUserAlreadyExistsException() {
        when(userRepository.existsByEmail("mayank@gmail.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );
        assertEquals("Email already registered: mayank@gmail.com", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenEmailAlreadyExists_register_shouldNeverEncodePassword() {
        when(userRepository.existsByEmail("mayank@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));

        verify(passwordEncoder, never()).encode(anyString());
    }

    // login TESTS 

    @Test
    void whenValidCredentials_login_shouldReturnAuthResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // authentication passes
        when(userRepository.findByEmail("mayank@gmail.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Mayank", response.getName());
        assertEquals("mayank@gmail.com", response.getEmail());
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("Login successful!", response.getMessage());
    }

    @Test
    void whenValidCredentials_login_shouldGenerateJwtToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("mayank@gmail.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        authService.login(loginRequest);

        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
    }

    @Test
    void whenWrongCredentials_login_shouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void whenUserNotFoundAfterAuth_login_shouldThrowRuntimeException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("mayank@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void whenValidLogin_shouldAuthenticateWithCorrectCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("mayank@gmail.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake.jwt.token");

        authService.login(loginRequest);

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken("mayank@gmail.com", "password123")
        );
    }
}
