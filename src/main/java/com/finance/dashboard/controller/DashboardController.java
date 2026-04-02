package com.finance.dashboard.controller;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.MonthlySummaryResponse;
import com.finance.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Dashboard endpoints.
 * VIEWER can see the summary.
 * ANALYST and ADMIN can see all dashboard data including detailed breakdowns.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated financial insights")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "Full dashboard snapshot — income, expense, net, last 5 transactions (ALL roles)")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/income")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "Total income (ALL roles)")
    public ResponseEntity<Map<String, BigDecimal>> getTotalIncome() {
        return ResponseEntity.ok(Map.of("totalIncome", dashboardService.getTotalIncome()));
    }

    @GetMapping("/expense")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "Total expense (ALL roles)")
    public ResponseEntity<Map<String, BigDecimal>> getTotalExpense() {
        return ResponseEntity.ok(Map.of("totalExpense", dashboardService.getTotalExpense()));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @Operation(summary = "Net balance = income − expense (ALL roles)")
    public ResponseEntity<Map<String, BigDecimal>> getNetBalance() {
        return ResponseEntity.ok(Map.of("netBalance", dashboardService.getNetBalance()));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Category-wise totals sorted descending (ADMIN, ANALYST)")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryTotals() {
        return ResponseEntity.ok(dashboardService.getCategoryTotals());
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Month-by-month income/expense breakdown (ADMIN, ANALYST)")
    public ResponseEntity<List<MonthlySummaryResponse>> getMonthlySummary() {
        return ResponseEntity.ok(dashboardService.getMonthlySummary());
    }
}