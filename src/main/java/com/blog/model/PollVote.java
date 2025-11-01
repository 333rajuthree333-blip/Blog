package com.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "poll_votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User identifier (IP address for anonymous users, or user ID if logged in)
    @Column(nullable = false, length = 100)
    private String voterIdentifier;

    // Relationship with poll option
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private PollOption selectedOption;

    // Relationship with poll (for easier queries)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private BlogPoll poll;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 200)
    private String userAgent;

    @CreationTimestamp
    private LocalDateTime votedAt;

    // Prevent duplicate votes from same user on same poll
    public static String generateVoterId(String ipAddress, String userAgent) {
        // Create a hash-like identifier from IP and user agent
        return (ipAddress + userAgent).hashCode() + "";
    }
}
