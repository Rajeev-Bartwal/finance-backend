package com.finance.dto.request;

import com.finance.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Fields to update on a user — all fields are optional")
public class UpdateUserRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "New username", example = "john_updated")
    private String username;

    @Email(message = "Email must be a valid address")
    @Schema(description = "New email address", example = "john_new@example.com")
    private String email;

    @Schema(description = "New role", example = "ANALYST", allowableValues = {"VIEWER", "ANALYST", "ADMIN"})
    private Role role;
}
