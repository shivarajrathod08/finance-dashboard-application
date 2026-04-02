package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.MonthlySummaryResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {

    DashboardSummaryResponse getSummary();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();

    BigDecimal getNetBalance();

    Map<String, BigDecimal> getCategoryTotals();

    List<MonthlySummaryResponse> getMonthlySummary();
}