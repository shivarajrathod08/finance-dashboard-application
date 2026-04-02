package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.request.FinancialRecordRequest;
import com.finance.dashboard.dto.response.FinancialRecordResponse;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Override
    public FinancialRecordResponse createRecord(FinancialRecordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .date(request.getDate())
                .description(request.getDescription())
                .createdBy(user)
                .deleted(false)
                .build();

        FinancialRecord saved = recordRepository.save(record);
        log.info("Created financial record id={} type={} amount={} by '{}'",
                saved.getId(), saved.getType(), saved.getAmount(), username);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Long id) {
        return toResponse(findActiveRecord(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FinancialRecordResponse> getAllRecords(Pageable pageable) {
        return toPagedResponse(recordRepository.findByDeletedFalse(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FinancialRecordResponse> getFilteredRecords(
            TransactionType type, String category, LocalDate from, LocalDate to, Pageable pageable) {
        Page<FinancialRecord> page = recordRepository.findWithFilters(type, category, from, to, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FinancialRecordResponse> searchRecords(String query, Pageable pageable) {
        return toPagedResponse(recordRepository.search(query, pageable));
    }

    @Override
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = findActiveRecord(id);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory().trim());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());
        log.info("Updated financial record id={}", id);
        return toResponse(recordRepository.save(record));
    }

    @Override
    public void softDeleteRecord(Long id) {
        FinancialRecord record = findActiveRecord(id);
        record.setDeleted(true);
        recordRepository.save(record);
        log.info("Soft-deleted financial record id={}", id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private FinancialRecord findActiveRecord(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialRecord", "id", id));
    }

    public FinancialRecordResponse toResponse(FinancialRecord r) {
        return FinancialRecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .description(r.getDescription())
                .createdBy(r.getCreatedBy() != null ? r.getCreatedBy().getUsername() : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private PagedResponse<FinancialRecordResponse> toPagedResponse(Page<FinancialRecord> page) {
        return PagedResponse.<FinancialRecordResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}