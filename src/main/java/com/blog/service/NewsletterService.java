package com.blog.service;

import com.blog.dto.NewsletterSubscriptionRequest;
import com.blog.model.BlogPost;
import com.blog.model.NewsletterSubscription;
import com.blog.repository.NewsletterSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsletterService {

    private final NewsletterSubscriptionRepository subscriptionRepository;

    /**
     * Subscribe to newsletter
     */
    public NewsletterSubscription subscribe(NewsletterSubscriptionRequest request, String ipAddress, String source) {
        log.info("Processing newsletter subscription for email: {}", request.getEmail());

        // Check if already subscribed
        if (subscriptionRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already subscribed to newsletter");
        }

        // Generate confirmation token
        String confirmationToken = UUID.randomUUID().toString();

        NewsletterSubscription subscription = NewsletterSubscription.builder()
                .email(request.getEmail().trim().toLowerCase())
                .name(request.getName() != null ? request.getName().trim() : null)
                .active(true)
                .confirmed(false) // Require confirmation
                .confirmationToken(confirmationToken)
                .ipAddress(ipAddress)
                .subscriptionSource(source)
                .subscribedAt(LocalDateTime.now())
                .build();

        NewsletterSubscription saved = subscriptionRepository.save(subscription);
        log.info("Newsletter subscription created for: {}", saved.getEmail());

        // TODO: Send confirmation email
        // sendConfirmationEmail(saved);

        return saved;
    }

    /**
     * Confirm subscription with token
     */
    public NewsletterSubscription confirmSubscription(String token) {
        Optional<NewsletterSubscription> subscriptionOpt = subscriptionRepository.findByConfirmationToken(token);

        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Invalid confirmation token");
        }

        NewsletterSubscription subscription = subscriptionOpt.get();

        if (subscription.getConfirmed()) {
            throw new RuntimeException("Subscription already confirmed");
        }

        subscription.setConfirmed(true);
        subscription.setConfirmedAt(LocalDateTime.now());

        return subscriptionRepository.save(subscription);
    }

    /**
     * Unsubscribe from newsletter
     */
    public void unsubscribe(String email) {
        Optional<NewsletterSubscription> subscriptionOpt = subscriptionRepository
                .findByEmail(email)
                .filter(NewsletterSubscription::getActive);

        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Active subscription not found for email: " + email);
        }

        NewsletterSubscription subscription = subscriptionOpt.get();
        subscription.setActive(false);

        subscriptionRepository.save(subscription);
        log.info("Unsubscribed email: {}", email);
    }

    /**
     * Get all active confirmed subscriptions
     */
    @Transactional(readOnly = true)
    public List<NewsletterSubscription> getActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrueAndConfirmedTrue();
    }

    /**
     * Get subscription statistics
     */
    @Transactional(readOnly = true)
    public NewsletterStats getStats() {
        long totalActive = subscriptionRepository.countByActiveTrue();
        long totalConfirmed = subscriptionRepository.countByActiveTrueAndConfirmedTrue();

        return NewsletterStats.builder()
                .totalActive(totalActive)
                .totalConfirmed(totalConfirmed)
                .totalUnconfirmed(totalActive - totalConfirmed)
                .build();
    }

    /**
     * Send newsletter to all active subscribers
     * This would integrate with email service (Mailchimp, SendGrid, etc.)
     */
    public void sendNewsletter(BlogPost newPost) {
        List<NewsletterSubscription> subscribers = getActiveSubscriptions();

        log.info("Sending newsletter for post '{}' to {} subscribers",
                newPost.getTitle(), subscribers.size());

        // TODO: Integrate with email service
        // For each subscriber, send email with new post content
        for (NewsletterSubscription subscriber : subscribers) {
            // sendEmail(subscriber, newPost);
            subscriber.setLastEmailSent(LocalDateTime.now());
        }

        subscriptionRepository.saveAll(subscribers);
    }

    // Helper method to find by email (not in repository)
    private Optional<NewsletterSubscription> findByEmail(String email) {
        return subscriptionRepository.findAll().stream()
                .filter(sub -> sub.getEmail().equalsIgnoreCase(email) && sub.getActive())
                .findFirst();
    }

    // Stats DTO
    @lombok.Data
    @lombok.Builder
    public static class NewsletterStats {
        private long totalActive;
        private long totalConfirmed;
        private long totalUnconfirmed;
    }
}
