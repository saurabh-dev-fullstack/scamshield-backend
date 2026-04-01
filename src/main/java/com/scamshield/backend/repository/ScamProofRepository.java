package com.scamshield.backend.repository;

import com.scamshield.backend.entity.ScamProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScamProofRepository extends JpaRepository<ScamProof, Long> {
    List<ScamProof> findByReportId(Long reportId);
}
