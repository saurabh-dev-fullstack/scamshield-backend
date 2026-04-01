package com.scamshield.backend.repository;

import com.scamshield.backend.entity.ScamArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScamArticleRepository extends JpaRepository<ScamArticle, Long> {
    List<ScamArticle> findByCategoryIgnoreCase(String category);
    ScamArticle findBySlug(String slug);
}
