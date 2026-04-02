package com.finance.dashboard.repository;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    /** Fetch by id only if not soft-deleted. */
    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    /** Paginated list of active records. */
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    // ──────────────────────────────────────────────────
    // FILTER QUERIES
    // ──────────────────────────────────────────────────

    Page<FinancialRecord> findByDeletedFalseAndType(TransactionType type, Pageable pageable);

    Page<FinancialRecord> findByDeletedFalseAndCategoryIgnoreCase(String category, Pageable pageable);

    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND r.date BETWEEN :from AND :to
        """)
    Page<FinancialRecord> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);

    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND (:type IS NULL OR r.type = :type)
          AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))
          AND (:from IS NULL OR r.date >= :from)
          AND (:to   IS NULL OR r.date <= :to)
        """)
    Page<FinancialRecord> findWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);

    // ──────────────────────────────────────────────────
    // DASHBOARD / AGGREGATE QUERIES
    // ──────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.deleted = false AND r.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    /**
     * Returns rows of [category, sum] for all active records of a given type.
     */
    @Query("""
        SELECT r.category, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
        GROUP BY r.category
        ORDER BY SUM(r.amount) DESC
        """)
    List<Object[]> sumByCategoryAndType(@Param("type") TransactionType type);

    /**
     * Returns rows of [category, sum] for ALL active records regardless of type.
     */
    @Query("""
        SELECT r.category, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false
        GROUP BY r.category
        ORDER BY SUM(r.amount) DESC
        """)
    List<Object[]> sumByCategory();

    /**
     * Monthly summary: returns rows of [year, month, type, total].
     */
    @Query("""
        SELECT YEAR(r.date), MONTH(r.date), r.type, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false
        GROUP BY YEAR(r.date), MONTH(r.date), r.type
        ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC
        """)
    List<Object[]> monthlySummary();

    /** Last N active transactions ordered by date descending, then by created timestamp. */
    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
        ORDER BY r.date DESC, r.createdAt DESC
        """)
    List<FinancialRecord> findLastNRecords(Pageable pageable);

    /** Full-text search across category and description. */
    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND (LOWER(r.category)    LIKE LOWER(CONCAT('%', :query, '%'))
           OR  LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<FinancialRecord> search(@Param("query") String query, Pageable pageable);
}