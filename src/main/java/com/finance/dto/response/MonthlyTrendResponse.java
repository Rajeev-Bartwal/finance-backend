package com.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MonthlyTrendResponse {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
}
