package com.finance.controllers;

import com.finance.dto.request.SetStatusRequest;
import com.finance.dto.request.UpdateUserRequest;
import com.finance.dto.response.ApiResponse;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.UserResponse;
import com.finance.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User management — Admin only")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
        summary = "List all users",
        description = "Returns a paginated list of all users. Sorted by newest first."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User list returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> list(
            @Parameter(description = "Page number, starts from 1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of results per page") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update user",
        description = "Update username, email, or role. Only fields included in the request body are changed."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Activate or deactivate user",
        description = "Set active to false to block the user from logging in. Their data is preserved."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> setStatus(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody SetStatusRequest request) {
        UserResponse user = userService.setStatus(id, request);
        String msg = Boolean.TRUE.equals(request.getActive()) ? "User activated" : "User deactivated";
        return ResponseEntity.ok(ApiResponse.success(msg, user));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete user",
        description = "Soft-deletes the user. They are hidden from all queries but the record is kept in the database."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
