package com.finance.dashboard.entity;

import com.finance.dashboard.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single financial transaction.
 *
 * Design decisions:
 * - BigDecimal for amount: avoids floating-point precision issues with money.
 * - soft-deleted flag: records are never physically deleted, enabling audit trails.
 * - Compound index on (type, category, date) to accelerate the most common filter queries.
 * - createdBy is a lazy-loaded ManyToOne to User; avoids N+1 in list queries when
 *   only record data is needed.
 */
@Entity
@Table(
        name = "financial_records",
        indexes = {
                @Index(name = "idx_records_type", columnList = "type"),
                @Index(name = "idx_records_category", columnList = "category"),
                @Index(name = "idx_records_date", columnList = "date"),
                @Index(name = "idx_records_deleted", columnList = "deleted"),
                @Index(name = "idx_records_type_category_date", columnList = "type, category, date")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "createdBy")
@ToString(exclude = "createdBy")
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    /** Soft-delete flag — never physically remove financial records. */
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}