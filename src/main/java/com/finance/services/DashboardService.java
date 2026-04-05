package com.finance.services;

import com.finance.dto.response.*;
import com.finance.enums.TransactionType;
import com.finance.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public SummaryResponse getSummary() {
        BigDecimal totalIncome   = transactionRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByType(TransactionType.EXPENSE);
        long       totalRecords  = transactionRepository.countAll();

        return SummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .totalRecords(totalRecords)
                .build();
    }

    public List<CategoryBreakdownResponse> getCategoryBreakdown() {
        return transactionRepository.getCategoryBreakdown().stream()
                .map(row -> new CategoryBreakdownResponse(
                        (String) row[0],
                        ((TransactionType) row[1]),
                        toBigDecimal(row[2]),
                        toLong(row[3])
                ))
                .toList();
    }

    public List<MonthlyTrendResponse> getMonthlyTrends(int months) {
        String since = LocalDate.now()
                .minusMonths(months)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Object[]> rows = transactionRepository.getMonthlyTrends(since);

        Map<String, MonthlyTrendAccumulator> map = new TreeMap<>();
        for (Object[] row : rows) {
            String month     = (String) row[0];
            TransactionType type = TransactionType.valueOf((String) row[1]);

            BigDecimal total = toBigDecimal(row[2]);

            map.computeIfAbsent(month, MonthlyTrendAccumulator::new);

            if (type == TransactionType.INCOME) {
                map.get(month).income = total;
            } else {
                map.get(month).expense = total;
            }

        }

        return map.values().stream()
                .map(acc -> new MonthlyTrendResponse(
                        acc.month,
                        acc.income,
                        acc.expense,
                        acc.income.subtract(acc.expense)
                ))
                .toList();
    }

    public List<TransactionResponse> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        return new BigDecimal(value.toString());
    }

    private long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long l) return l;
        return Long.parseLong(value.toString());
    }

    private static class MonthlyTrendAccumulator {
        String month;
        BigDecimal income  = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        MonthlyTrendAccumulator(String month) {
            this.month = month;
        }
    }
}
