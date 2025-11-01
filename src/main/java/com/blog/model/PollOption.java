package com.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "poll_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String optionText;

    @Column(length = 500)
    private String description;

    @Builder.Default
    private Integer voteCount = 0;

    @Builder.Default
    private Double percentage = 0.0;

    // Relationship with poll
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private BlogPoll poll;

    // Calculate percentage based on total votes
    public void calculatePercentage(int totalVotes) {
        if (totalVotes > 0) {
            this.percentage = (double) voteCount / totalVotes * 100;
        } else {
            this.percentage = 0.0;
        }
    }
}
