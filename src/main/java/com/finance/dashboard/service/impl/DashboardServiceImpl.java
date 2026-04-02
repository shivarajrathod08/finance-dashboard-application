package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.FinancialRecordResponse;
import com.finance.dashboard.dto.response.MonthlySummaryResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard aggregation service.
 *
 * Design notes:
 * - All queries are read-only (@Transactional(readOnly=true)) so Hibernate
 *   skips dirty-checking overhead and can use a read replica if configured.
 * - Monthly summary merges INCOME + EXPENSE rows per month in Java rather
 *   than a single complex SQL pivot, trading one extra round-trip for simpler
 *   JPQL and easier future caching.
 * - Category totals are sorted descending by amount so the UI can render
 *   them as-is without additional sorting.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int LAST_N_TRANSACTIONS = 5;

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordServiceImpl recordService; // reuse toResponse mapper

    @Override
    public DashboardSummaryResponse getSummary() {
        BigDecimal income  = getTotalIncome();
        BigDecimal expense = getTotalExpense();
        BigDecimal net     = income.subtract(expense);

        List<FinancialRecord> lastFive = recordRepository.findLastNRecords(
                PageRequest.of(0, LAST_N_TRANSACTIONS));

        List<FinancialRecordResponse> lastFiveDto = lastFive.stream()
                .map(recordService::toResponse)
                .toList();

        return DashboardSummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .netBalance(net)
                .categoryTotals(getCategoryTotals())
                .lastFiveTransactions(lastFiveDto)
                .build();
    }

    @Override
    public BigDecimal getTotalIncome() {
        return recordRepository.sumByType(TransactionType.INCOME);
    }

    @Override
    public BigDecimal getTotalExpense() {
        return recordRepository.sumByType(TransactionType.EXPENSE);
    }

    @Override
    public BigDecimal getNetBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    @Override
    public Map<String, BigDecimal> getCategoryTotals() {
        List<Object[]> rows = recordRepository.sumByCategory();
        // LinkedHashMap preserves DB-returned ordering (DESC by sum)
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (BigDecimal) row[1]);
        }
        return result;
    }

    @Override
    public List<MonthlySummaryResponse> getMonthlySummary() {
        List<Object[]> rows = recordRepository.monthlySummary();

        // Key: "YYYY-MM"  →  accumulate income & expense separately
        record MonthKey(int year, int month) {}

        Map<MonthKey, BigDecimal[]> accumulator = new LinkedHashMap<>();

        for (Object[] row : rows) {
            int year              = ((Number) row[0]).intValue();
            int month             = ((Number) row[1]).intValue();
            TransactionType type  = TransactionType.valueOf(row[2].toString());
            BigDecimal amount     = (BigDecimal) row[3];

            MonthKey key = new MonthKey(year, month);
            accumulator.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});

            if (type == TransactionType.INCOME) {
                accumulator.get(key)[0] = accumulator.get(key)[0].add(amount);
            } else {
                accumulator.get(key)[1] = accumulator.get(key)[1].add(amount);
            }
        }

        return accumulator.entrySet().stream()
                .map(entry -> {
                    int year    = entry.getKey().year();
                    int month   = entry.getKey().month();
                    BigDecimal income  = entry.getValue()[0];
                    BigDecimal expense = entry.getValue()[1];
                    return MonthlySummaryResponse.builder()
                            .year(year)
                            .month(month)
                            .monthName(Month.of(month).name())
                            .totalIncome(income)
                            .totalExpense(expense)
                            .netBalance(income.subtract(expense))
                            .build();
                })
                .collect(Collectors.toList());
    }
}