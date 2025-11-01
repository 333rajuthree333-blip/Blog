package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blog_posts")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String excerpt;

    @Column(length = 200)
    private String titleBn;

    @Column(columnDefinition = "TEXT")
    private String contentBn;

    @Column(length = 500)
    private String excerptBn;

    @Column(length = 200)
    private String titleHi;

    @Column(columnDefinition = "TEXT")
    private String contentHi;

    @Column(length = 500)
    private String excerptHi;

    @Column(length = 100)
    private String author;

    @ElementCollection
    @CollectionTable(name = "blog_post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "blog_post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url", length = 500)
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(nullable = false)
    private Boolean published = false;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        if (excerpt == null || excerpt.isEmpty()) {
            excerpt = generateExcerpt();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (published && publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
    }

    private String generateExcerpt() {
        if (content != null && content.length() > 200) {
            return content.substring(0, 197) + "...";
        }
        return content;
    }
}
