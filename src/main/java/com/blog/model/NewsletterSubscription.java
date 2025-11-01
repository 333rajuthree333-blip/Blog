package com.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 100)
    private String name;

    @Builder.Default
    private Boolean active = true;

    @Column(length = 500)
    private String preferences; // JSON string for subscription preferences

    @Column(length = 45)
    private String ipAddress;

    @CreationTimestamp
    private LocalDateTime subscribedAt;

    @Column
    private LocalDateTime lastEmailSent;

    @Column
    private String subscriptionSource; // e.g., "homepage", "sidebar", "footer"

    @Builder.Default
    private Boolean confirmed = false; // For double opt-in

    @Column(length = 100)
    private String confirmationToken;

    @Column
    private LocalDateTime confirmedAt;
}
