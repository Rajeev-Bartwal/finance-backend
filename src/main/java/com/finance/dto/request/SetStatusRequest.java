package com.finance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "Activate or deactivate a user account")
public class SetStatusRequest {

    @NotNull(message = "isActive field is required")
    @Schema(description = "Set to true to activate, false to deactivate", example = "false")
    private Boolean active;
}
