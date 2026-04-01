package com.scamshield.backend.controller;

import com.scamshield.backend.entity.ScamProof;
import com.scamshield.backend.entity.ScamReport;
import com.scamshield.backend.repository.ScamProofRepository;
import com.scamshield.backend.repository.ScamReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/scams")
public class ScamReportController {

    @Autowired
    private ScamReportRepository repo;
    @Autowired
    private ScamProofRepository proofRepo;




    // admin conteroller here

    // List pending scams (Admin)
    @GetMapping("/admin/pending")
    public List<ScamReport> pending() {
        return repo.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // Approve a scam
    @PutMapping("/{id}/approve")
    public ScamReport approve(@PathVariable Long id) {
        ScamReport report = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus("APPROVED");
        return repo.save(report);
    }

    @GetMapping("/{id}")
    public ScamReport getOne(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Scam not found"));
    }


    // Reject a scam
    @PutMapping("/{id}/reject")
    public ScamReport reject(@PathVariable Long id) {
        ScamReport report = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus("REJECTED");
        return repo.save(report);
    }

    // Submit scam
    @PostMapping
    public ScamReport submit(@RequestBody ScamReport report) {
        report.setStatus("PENDING");
        return repo.save(report);
    }

    // Public feed
    @GetMapping("/feed")
    public List<ScamReport> feed() {
        return repo.findByStatusOrderByCreatedAtDesc("APPROVED");
    }

    @PostMapping("/{id}/proofs")
    public ResponseEntity<?> uploadMultipleProofs(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files
    ) {
        try {
            // ✅ ALWAYS use relative folder (NOT Tomcat temp)
            Path uploadDir = Paths.get("uploads", "proofs");

            // ✅ CREATE FOLDER IF NOT EXISTS
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String fileName =
                        System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path targetPath = uploadDir.resolve(fileName);

                // ✅ WRITE FILE SAFELY
                Files.copy(
                        file.getInputStream(),
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                // TODO: Save fileName + scamId into DB here
            }

            return ResponseEntity.ok("Proofs uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Proof upload failed");
        }
    }






    @GetMapping("/search")
    public List<ScamReport> search(@RequestParam String q) {
        return repo.search(q);
    }

    @GetMapping("/category")
    public List<ScamReport> byCategory(@RequestParam String type) {
        return repo.findByStatusAndScamTypeIgnoreCaseOrderByCreatedAtDesc(
                "APPROVED", type
        );
    }
    @GetMapping("/trending")
    public List<ScamReport> trending() {
        return repo.findTop5ByStatusOrderByCreatedAtDesc("APPROVED");
    }

    @GetMapping("/state")
    public List<ScamReport> byState(@RequestParam String name) {
        return repo.findByStatusAndStateIgnoreCaseOrderByCreatedAtDesc(
                "APPROVED", name
        );
    }

    @GetMapping("/city")
    public List<ScamReport> byCity(@RequestParam String name) {
        return repo.findByStatusAndCityIgnoreCaseOrderByCreatedAtDesc(
                "APPROVED", name
        );
    }
    @PostMapping("/admin/approve/{id}")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestHeader("X-ADMIN-KEY") String key
    ) {
        if (!key.equals("SECRET_ADMIN_KEY")) {
            return ResponseEntity.status(403).build();
        }

        ScamReport report = repo.findById(id).orElseThrow();
        report.setStatus("APPROVED");
        repo.save(report);

        return ResponseEntity.ok("Approved");
    }


}
