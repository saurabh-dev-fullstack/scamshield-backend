package com.scamshield.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "scam_reports")
public class ScamReport {

    @JsonManagedReference   // ✅ allows proofs in JSON
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ScamProof> proofs;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scamType;
    private String platform;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double amountLost;
    private String city;
    private String state;

    private Boolean anonymous = true;

    // USER / ADMIN
    private String source;

    // PENDING / APPROVED / REJECTED
    private String status;

    // image uploaded by admin or user (optional)
    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getScamType() { return scamType; }
    public void setScamType(String scamType) { this.scamType = scamType; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAmountLost() { return amountLost; }
    public void setAmountLost(Double amountLost) { this.amountLost = amountLost; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Boolean getAnonymous() { return anonymous; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ScamProof> getProofs() { return proofs; }
    public void setProofs(List<ScamProof> proofs) { this.proofs = proofs; }
}