package com.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blog_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String authorEmail;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String postTitle;

    @Builder.Default
    private Integer upvotes = 0;

    @Builder.Default
    private Boolean approved = true;

    @Column(length = 500)
    private String authorWebsite;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // For nested replies (optional feature)
    @Column
    private Long parentCommentId;

    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BlogComment> replies = new ArrayList<>();

    // IP address for spam prevention (optional)
    @Column(length = 45)
    private String ipAddress;
}
