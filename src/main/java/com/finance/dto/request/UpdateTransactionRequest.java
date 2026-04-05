package com.finance.dto.request;

import com.finance.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Schema(description = "Fields to update on a transaction — all fields are optional")
public class UpdateTransactionRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    @Schema(description = "New amount", example = "7500.00")
    private BigDecimal amount;

    @Schema(description = "New type", example = "EXPENSE", allowableValues = {"INCOME", "EXPENSE"})
    private TransactionType type;

    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    @Schema(description = "New category", example = "Rent")
    private String category;

    @Schema(description = "New date in YYYY-MM-DD format", example = "2024-04-01")
    private LocalDate date;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Updated notes", example = "Revised monthly rent")
    private String notes;
}
