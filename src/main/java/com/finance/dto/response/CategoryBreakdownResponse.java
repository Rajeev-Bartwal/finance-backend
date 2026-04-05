package com.finance.dto.response;

import com.finance.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CategoryBreakdownResponse {
    private String category;
    private TransactionType type;
    private BigDecimal total;
    private long count;
}
