package com.scamshield.backend.controller;

import com.scamshield.backend.entity.ScamArticle;
import com.scamshield.backend.repository.ScamArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ScamArticleController {

    private final ScamArticleRepository articleRepo;

    public ScamArticleController(ScamArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    // 🌍 PUBLIC: Get articles by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ScamArticle>> getByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                articleRepo.findByCategoryIgnoreCase(category)
        );
    }

    // 🌍 PUBLIC: Get article by slug (SEO PAGE)
    @GetMapping("/{slug}")
    public ResponseEntity<ScamArticle> getBySlug(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(
                articleRepo.findBySlug(slug)
        );
    }
}
