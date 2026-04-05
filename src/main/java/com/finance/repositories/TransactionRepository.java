package com.finance.repositories;

import com.finance.enums.TransactionType;
import com.finance.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT t FROM Transaction t
            WHERE (:type IS NULL OR t.type = :type)
              AND (:category IS NULL OR LOWER(t.category) LIKE LOWER(CONCAT('%', :category, '%')))
              AND (:startDate IS NULL OR t.date >= :startDate)
              AND (:endDate IS NULL OR t.date <= :endDate)
              AND (:search IS NULL
                    OR LOWER(t.notes) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(t.category) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY t.date DESC, t.createdAt DESC
            """)
    Page<Transaction> findAllWithFilters(
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAll();

    @Query("""
            SELECT t.category, t.type, SUM(t.amount) AS total, COUNT(t) AS count
            FROM Transaction t
            GROUP BY t.category, t.type
            ORDER BY total DESC
            """)
    List<Object[]> getCategoryBreakdown();

    @Query(value = """
            SELECT FORMATDATETIME(date, 'yyyy-MM') AS month_val,
                  type,
                  SUM(amount) AS total
           FROM transactions
           WHERE deleted_at IS NULL
             AND date >= :since
           GROUP BY month_val, type
           ORDER BY month_val ASC
            """, nativeQuery = true)
    List<Object[]> getMonthlyTrends(@Param("since") String since);

    List<Transaction> findTop10ByOrderByCreatedAtDesc();
}
