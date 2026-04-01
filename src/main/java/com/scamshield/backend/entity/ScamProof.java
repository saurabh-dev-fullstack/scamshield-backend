package com.scamshield.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "scam_proofs")
public class ScamProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @JsonBackReference   // ✅ prevents infinite loop
    @ManyToOne
    @JoinColumn(name = "report_id")
    private ScamReport report;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public ScamReport getReport() { return report; }
    public void setReport(ScamReport report) { this.report = report; }
}