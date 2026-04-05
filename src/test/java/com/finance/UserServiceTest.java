package com.finance;

import com.finance.dto.request.SetStatusRequest;
import com.finance.dto.request.UpdateUserRequest;
import com.finance.dto.response.UserResponse;
import com.finance.enums.Role;
import com.finance.exception.DuplicateResourceException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.models.User;
import com.finance.repositories.UserRepository;
import com.finance.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    private User sampleUser() {
        return User.builder()
                .id(1L)
                .username("rajeev")
                .email("rajeev@test.com")
                .role(Role.VIEWER)
                .active(true)
                .build();
    }

    @Test
    void getById_returnsUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));

        UserResponse response = userService.getById(1L);

        assertThat(response.getUsername()).isEqualTo("rajeev");
        assertThat(response.getRole()).isEqualTo(Role.VIEWER);
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_changesRole_whenProvided() {
        User user = sampleUser();
        UpdateUserRequest req = mock(UpdateUserRequest.class);
        when(req.getUsername()).thenReturn(null);
        when(req.getEmail()).thenReturn(null);
        when(req.getRole()).thenReturn(Role.ANALYST);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.update(1L, req);

        assertThat(user.getRole()).isEqualTo(Role.ANALYST);
    }

    @Test
    void update_throwsDuplicate_whenUsernameAlreadyTaken() {
        User user = sampleUser();
        UpdateUserRequest req = mock(UpdateUserRequest.class);
        when(req.getUsername()).thenReturn("existingUser");
        when(req.getEmail()).thenReturn(null);
        when(req.getRole()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("existingUser");
    }

    @Test
    void setStatus_deactivatesUser() {
        User user = sampleUser();
        SetStatusRequest req = mock(SetStatusRequest.class);
        when(req.getActive()).thenReturn(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.setStatus(1L, req);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void delete_callsRepositoryDelete() {
        User user = sampleUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }
}
