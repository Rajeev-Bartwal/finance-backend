package com.finance.dto.request;

import com.finance.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Schema(description = "Details for creating a new transaction")
public class CreateTransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    @Schema(description = "Transaction amount (must be positive)", example = "5000.00")
    private BigDecimal amount;

    @NotNull(message = "Type is required — must be INCOME or EXPENSE")
    @Schema(description = "Transaction type", example = "INCOME", allowableValues = {"INCOME", "EXPENSE"})
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    @Schema(description = "Category name", example = "Salary")
    private String category;

    @NotNull(message = "Date is required — format: YYYY-MM-DD")
    @Schema(description = "Transaction date in YYYY-MM-DD format", example = "2024-03-15")
    private LocalDate date;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Optional notes or description", example = "Monthly salary credit")
    private String notes;
}
