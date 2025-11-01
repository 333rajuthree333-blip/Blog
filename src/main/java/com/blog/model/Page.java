package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "pages")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Page title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String titleEn;

    @Column(length = 200)
    private String titleHi;

    @NotBlank(message = "Page slug is required")
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentBn;

    @Column(columnDefinition = "TEXT")
    private String contentHi;

    @Column(length = 500)
    private String metaDescription;

    @Column(nullable = false)
    private Boolean published = true;

    @Column(name = "show_in_footer")
    private Boolean showInFooter = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (slug == null || slug.isEmpty()) {
            slug = title.toLowerCase()
                    .replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
        }
    }
}
