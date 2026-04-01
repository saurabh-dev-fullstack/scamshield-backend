package com.scamshield.backend.repository;

import java.util.List;

import com.scamshield.backend.entity.ScamReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScamReportRepository extends JpaRepository<ScamReport, Long> {

    // =====================================================
    // 🔴 ADMIN DASHBOARD (USER-REPORTED SCAMS ONLY)
    // =====================================================

    // Pending USER scams only (IMPORTANT FIX)
    List<ScamReport> findBySourceAndStatusOrderByCreatedAtDesc(
            String source,   // "USER"
            String status    // "PENDING"
    );

    // =====================================================
    // 🌍 PUBLIC WEBSITE (APPROVED SCAMS)
    // =====================================================

    // All approved scams (ADMIN + USER)
    List<ScamReport> findByStatusOrderByCreatedAtDesc(String status);

    // Latest 5 approved scams (homepage / feed)
    List<ScamReport> findTop5ByStatusOrderByCreatedAtDesc(String status);

    // Category-wise approved scams
    List<ScamReport> findByStatusAndScamTypeIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String scamType
    );

    // Filter by State (approved only)
    List<ScamReport> findByStatusAndStateIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String state
    );

    // Filter by City (approved only)
    List<ScamReport> findByStatusAndCityIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String city
    );

    List<ScamReport> findTop5BySourceAndStatusOrderByCreatedAtDesc(
            String source,
            String status
    );


    // =====================================================
    // 🔍 SEARCH (APPROVED SCAMS ONLY)
    // =====================================================

    @Query("""
        SELECT s FROM ScamReport s
        WHERE s.status = 'APPROVED'
        AND (
            LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(s.platform) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(s.scamType) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(s.city) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(s.state) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        ORDER BY s.createdAt DESC
    """)
    List<ScamReport> search(@Param("q") String q);
}
