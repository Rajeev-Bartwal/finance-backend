package com.finance.controllers;

import com.finance.dto.response.*;
import com.finance.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Summary stats and analytics for the finance dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
        summary = "Financial summary",
        description = "Returns total income, total expenses, net balance, and the overall number of transaction records."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token missing or invalid")
    })
    public ResponseEntity<ApiResponse<SummaryResponse>> summary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(
        summary = "Recent transactions",
        description = "Returns the 10 most recently added transactions for the activity feed on the dashboard."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recent transactions returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token missing or invalid")
    })
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> recent() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentTransactions()));
    }

    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(
        summary = "Category breakdown",
        description = "Returns total amount and count per category grouped by type (INCOME / EXPENSE). Used for pie and bar charts."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Breakdown returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Analyst or Admin role required")
    })
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> categoryBreakdown() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCategoryBreakdown()));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(
        summary = "Monthly trends",
        description = "Returns monthly income and expense totals for the last N months. Used for line charts. Range: 1–24 months."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trends returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Analyst or Admin role required")
    })
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> trends(
            @Parameter(description = "How many months back to include (1–24, default 6)")
            @RequestParam(defaultValue = "6") int months) {
        if (months < 1 || months > 24) months = 6;
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getMonthlyTrends(months)));
    }
}
