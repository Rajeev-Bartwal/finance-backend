package com.finance;

import com.finance.dto.request.RegisterRequest;
import com.finance.dto.response.UserResponse;
import com.finance.enums.Role;
import com.finance.exception.DuplicateResourceException;
import com.finance.models.User;
import com.finance.repositories.UserRepository;
import com.finance.security.JwtUtil;
import com.finance.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = mock(RegisterRequest.class);
        when(registerRequest.getUsername()).thenReturn("rajeev");
        when(registerRequest.getEmail()).thenReturn("rajeev@test.com");
        when(registerRequest.getPassword()).thenReturn("password123");
        when(registerRequest.getRole()).thenReturn(null);
    }

    @Test
    void register_success_defaultsRoleToViewer() {
        when(userRepository.existsByUsername("rajeev")).thenReturn(false);
        when(userRepository.existsByEmail("rajeev@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = authService.register(registerRequest);

        assertThat(response.getUsername()).isEqualTo("rajeev");
        assertThat(response.getRole()).isEqualTo(Role.VIEWER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsDuplicate_whenUsernameExists() {
        when(userRepository.existsByUsername("rajeev")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("rajeev");
    }

    @Test
    void register_throwsDuplicate_whenEmailExists() {
        when(userRepository.existsByUsername("rajeev")).thenReturn(false);
        when(userRepository.existsByEmail("rajeev@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("rajeev@test.com");
    }

    @Test
    void register_usesProvidedRole_whenRoleIsGiven() {
        when(registerRequest.getRole()).thenReturn(Role.ANALYST);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserResponse response = authService.register(registerRequest);

        assertThat(response.getRole()).isEqualTo(Role.ANALYST);
    }
}
