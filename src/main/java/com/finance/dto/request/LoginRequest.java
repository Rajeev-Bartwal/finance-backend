package com.finance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Login credentials")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Your username", example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Your password", example = "password123")
    private String password;
}
