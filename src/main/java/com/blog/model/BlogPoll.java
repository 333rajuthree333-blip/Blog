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
@Table(name = "blog_polls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(length = 1000)
    private String description;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean allowMultipleVotes = false; // Allow users to change their vote

    @Builder.Default
    private Integer totalVotes = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    // Relationship with poll options
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PollOption> options = new ArrayList<>();

    // Check if poll is expired
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // Check if poll is active and not expired
    public boolean isAvailable() {
        return active && !isExpired();
    }
}
