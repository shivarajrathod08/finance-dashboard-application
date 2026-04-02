package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.FinancialRecordRequest;
import com.finance.dashboard.dto.response.FinancialRecordResponse;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "CRUD operations on financial transactions")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    // ── CREATE (ADMIN only) ───────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a financial record (ADMIN only)")
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordService.createRecord(request, authentication.getName()));
    }

    // ── READ (ADMIN + ANALYST) ────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Get a financial record by ID (ADMIN, ANALYST)")
    public ResponseEntity<FinancialRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "List all records with pagination & optional filters (ADMIN, ANALYST)")
    public ResponseEntity<PagedResponse<FinancialRecordResponse>> getAllRecords(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // If any filter is present, use the filtered query; otherwise return all
        boolean hasFilter = type != null || category != null || from != null || to != null;
        if (hasFilter) {
            return ResponseEntity.ok(
                    recordService.getFilteredRecords(type, category, from, to, pageable));
        }
        return ResponseEntity.ok(recordService.getAllRecords(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Full-text search in category and description (ADMIN, ANALYST)")
    public ResponseEntity<PagedResponse<FinancialRecordResponse>> searchRecords(
            @RequestParam String query,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(recordService.searchRecords(query, pageable));
    }

    // ── UPDATE (ADMIN only) ───────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a financial record (ADMIN only)")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    // ── DELETE / SOFT-DELETE (ADMIN only) ─────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a financial record (ADMIN only)")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}