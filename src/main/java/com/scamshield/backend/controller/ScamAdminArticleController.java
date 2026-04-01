package com.scamshield.backend.controller;

import com.scamshield.backend.entity.ScamArticle;
import com.scamshield.backend.repository.ScamArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/articles")
public class ScamAdminArticleController {

    private final ScamArticleRepository articleRepo;

    public ScamAdminArticleController(ScamArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    // 📝 ADMIN: Add article
    @PostMapping
    public ResponseEntity<ScamArticle> addArticle(@RequestBody ScamArticle article) {

        if (article.getSlug() == null || article.getSlug().isBlank()) {
            String slug = article.getTitle()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("(^-|-$)", "");
            article.setSlug(slug);
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setCreatedBy("ADMIN");

        return ResponseEntity.ok(articleRepo.save(article));
    }




    // 🗑️ ADMIN: Delete article
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {

        articleRepo.deleteById(id);
        return ResponseEntity.ok("Article deleted");
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ScamArticle> addArticle(
            @RequestPart("article") ScamArticle article,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/articles");

            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            article.setCoverImage("/uploads/articles/" + fileName);
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setCreatedBy("ADMIN");

        if (article.getSlug() == null || article.getSlug().isBlank()) {
            article.setSlug(article.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }

        return ResponseEntity.ok(articleRepo.save(article));
    }

}
