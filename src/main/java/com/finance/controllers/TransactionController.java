package com.finance.controllers;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.ApiResponse;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.TransactionResponse;
import com.finance.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Create, view, update, and delete financial records")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
        summary = "List transactions",
        description = "Returns a paginated list of transactions. Supports filtering by type, category, date range, and keyword search."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid filter value (e.g. wrong type or date format)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token missing or invalid")
    })
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> list(
            @Parameter(description = "Filter by type: INCOME or EXPENSE") @RequestParam(required = false) String type,
            @Parameter(description = "Partial match on category name") @RequestParam(required = false) String category,
            @Parameter(description = "Start date in YYYY-MM-DD format") @RequestParam(name = "start_date", required = false) String startDate,
            @Parameter(description = "End date in YYYY-MM-DD format")   @RequestParam(name = "end_date",   required = false) String endDate,
            @Parameter(description = "Keyword search on category or notes") @RequestParam(required = false) String search,
            @Parameter(description = "Page number, starts from 1") @RequestParam(defaultValue = "1")  int page,
            @Parameter(description = "Results per page, max 100")  @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getAll(type, category, startDate, endDate, search, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get transaction by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(
        summary = "Create transaction",
        description = "Add a new income or expense record. The logged-in user is recorded as the creator."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transaction created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Analyst or Admin role required")
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TransactionResponse tx = transactionService.create(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", tx));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(
        summary = "Update transaction",
        description = "Partial update — only the fields you include in the request body will be changed."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Analyst or Admin role required")
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully",
                transactionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete transaction",
        description = "Soft-deletes the record — it is hidden from queries but kept in the database for audit purposes."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
}
