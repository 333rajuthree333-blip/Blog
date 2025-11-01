package com.blog.repository;

import com.blog.model.NewsletterSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsletterSubscriptionRepository extends JpaRepository<NewsletterSubscription, Long> {

    // Check if email already exists
    boolean existsByEmail(String email);

    // Find active subscriptions
    List<NewsletterSubscription> findByActiveTrue();

    // Find confirmed subscriptions
    List<NewsletterSubscription> findByActiveTrueAndConfirmedTrue();

    // Find by confirmation token
    Optional<NewsletterSubscription> findByConfirmationToken(String token);
    
    // Find by email
    Optional<NewsletterSubscription> findByEmail(String email);

    // Count total active subscriptions
    long countByActiveTrue();

    // Count confirmed subscriptions
    long countByActiveTrueAndConfirmedTrue();
}
