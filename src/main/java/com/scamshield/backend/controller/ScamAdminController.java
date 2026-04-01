package com.scamshield.backend.controller;

import com.scamshield.backend.entity.ScamArticle;
import com.scamshield.backend.entity.ScamProof;
import com.scamshield.backend.entity.ScamReport;
import com.scamshield.backend.repository.ScamArticleRepository;
import com.scamshield.backend.repository.ScamProofRepository;
import com.scamshield.backend.repository.ScamReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ScamAdminController {

    private final ScamReportRepository reportRepo;
    private final ScamProofRepository proofRepo;
    private final ScamArticleRepository articleRepo;

    public ScamAdminController(
            ScamReportRepository reportRepo,
            ScamProofRepository proofRepo,
            ScamArticleRepository articleRepo
    ) {
        this.reportRepo = reportRepo;
        this.proofRepo = proofRepo;
        this.articleRepo = articleRepo;
    }

    /* =====================================================
       SCAM REPORT MANAGEMENT (ADMIN)
       ===================================================== */

    // 📋 View pending scam reports
    @GetMapping("/scams/pending")
    public ResponseEntity<List<ScamReport>> getPendingScams() {
        return ResponseEntity.ok(
                reportRepo.findByStatusOrderByCreatedAtDesc("PENDING")
        );
    }

    // ✅ Approve scam report
    @PostMapping("/scams/approve/{id}")
    public ResponseEntity<String> approveScam(@PathVariable Long id) {

        ScamReport scam = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Scam not found"));

        scam.setStatus("APPROVED");
        reportRepo.save(scam);

        return ResponseEntity.ok("Scam approved");
    }

    // ❌ Reject scam report
    @PostMapping("/scams/reject/{id}")
    public ResponseEntity<String> rejectScam(@PathVariable Long id) {

        ScamReport scam = reportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Scam not found"));

        scam.setStatus("REJECTED");
        reportRepo.save(scam);

        return ResponseEntity.ok("Scam rejected");
    }

    // 📸 Get proofs for a scam
    @GetMapping("/scams/{id}/proofs")
    public ResponseEntity<List<ScamProof>> getScamProofs(@PathVariable Long id) {
        return ResponseEntity.ok(
                proofRepo.findByReportId(id)
        );
    }
}
