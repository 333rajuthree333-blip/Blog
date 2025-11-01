package com.blog.service;

import com.blog.model.BlogPost;
import com.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final OpenRouterService openRouterService;
    private final NewsletterService newsletterService;

    /**
     * Create a new blog post manually
     */
    public BlogPost createPost(BlogPost post) {
        log.info("Creating new blog post: {}", post.getTitle());
        return blogPostRepository.save(post);
    }

    /**
     * Generate and create a blog post using AI
     */
    public BlogPost generateBlogPost(String prompt, String author) {
        log.info("Generating AI blog post with prompt: {}", prompt);
        
        try {
            // Generate content using OpenRouter in English
            String content = openRouterService.generateBlogPostContent(prompt);
            
            // Generate title in English
            String title = openRouterService.generateTitle(content);
            
            // Generate excerpt in English
            String excerpt = openRouterService.generateExcerpt(content);
            
            // Generate Bangla content
            String contentBn = openRouterService.generateBlogPostContentBn(prompt);
            String titleBn = openRouterService.generateTitleBn(contentBn);
            String excerptBn = openRouterService.generateExcerptBn(contentBn);
            
            // Generate Hindi content
            String contentHi = openRouterService.generateBlogPostContentHi(prompt);
            String titleHi = openRouterService.generateTitleHi(contentHi);
            String excerptHi = openRouterService.generateExcerptHi(contentHi);
            
            // Generate tags (same for all languages)
            String tagsString = openRouterService.generateTags(content);
            List<String> tags = Arrays.stream(tagsString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            
            // Create blog post
            BlogPost post = new BlogPost();
            post.setTitle(title);
            post.setContent(content);
            post.setExcerpt(excerpt);
            post.setTitleBn(titleBn);
            post.setContentBn(contentBn);
            post.setExcerptBn(excerptBn);
            post.setTitleHi(titleHi);
            post.setContentHi(contentHi);
            post.setExcerptHi(excerptHi);
            post.setAuthor(author != null ? author : "AI Assistant");
            post.setTags(tags);
            post.setPublished(false); // Draft by default
            post.setIsAiGenerated(true);
            post.setAiPrompt(prompt);
            
            BlogPost savedPost = blogPostRepository.save(post);
            log.info("AI-generated multilingual blog post created with ID: {}", savedPost.getId());
            
            return savedPost;
        } catch (Exception e) {
            log.error("Error generating blog post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate blog post: " + e.getMessage());
        }
    }

    /**
     * Get blog post by ID
     */
    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found with id: " + id));
    }

    /**
     * Get all published posts with pagination
     */
    public Page<BlogPost> getPublishedPosts(Pageable pageable) {
        return blogPostRepository.findByPublishedTrueOrderByCreatedAtDesc(pageable);
    }

    /**
     * Get all posts (admin)
     */
    public Page<BlogPost> getAllPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable);
    }

    /**
     * Search posts by keyword
     */
    public Page<BlogPost> searchPosts(String keyword, Pageable pageable) {
        return blogPostRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * Get posts by tag
     */
    public Page<BlogPost> getPostsByTag(String tag, Pageable pageable) {
        return blogPostRepository.findByTag(tag, pageable);
    }

    /**
     * Get top posts by views
     */
    public List<BlogPost> getTopPosts() {
        return blogPostRepository.findTop10ByPublishedTrueOrderByViewCountDesc();
    }

    /**
     * Update blog post
     */
    public BlogPost updatePost(Long id, BlogPost updatedPost) {
        BlogPost existingPost = getPostById(id);
        
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setExcerpt(updatedPost.getExcerpt());
        existingPost.setAuthor(updatedPost.getAuthor());
        existingPost.setTags(updatedPost.getTags());
        existingPost.setFeaturedImage(updatedPost.getFeaturedImage());
        
        if (updatedPost.getImageUrls() != null) {
            existingPost.setImageUrls(updatedPost.getImageUrls());
        }
        
        return blogPostRepository.save(existingPost);
    }

    /**
     * Publish or unpublish a post
     */
    public BlogPost togglePublish(Long id) {
        BlogPost post = getPostById(id);
        boolean wasPublished = post.getPublished();
        post.setPublished(!post.getPublished());

        if (post.getPublished() && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());

            // Send newsletter to subscribers when post is newly published
            if (!wasPublished) {
                try {
                    newsletterService.sendNewsletter(post);
                    log.info("Newsletter sent for newly published post: {}", post.getTitle());
                } catch (Exception e) {
                    log.error("Failed to send newsletter for post {}: {}", post.getId(), e.getMessage());
                    // Don't fail the publish operation if newsletter fails
                }
            }
        }

        return blogPostRepository.save(post);
    }

    /**
     * Increment view count
     */
    public void incrementViewCount(Long id) {
        BlogPost post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        blogPostRepository.save(post);
    }

    /**
     * Add image to post
     */
    public BlogPost addImageToPost(Long id, String imageUrl) {
        BlogPost post = getPostById(id);
        post.getImageUrls().add(imageUrl);
        return blogPostRepository.save(post);
    }

    /**
     * Set featured image
     */
    public BlogPost setFeaturedImage(Long id, String imageUrl) {
        BlogPost post = getPostById(id);
        post.setFeaturedImage(imageUrl);
        return blogPostRepository.save(post);
    }

    /**
     * Delete blog post
     */
    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
        log.info("Deleted blog post with id: {}", id);
    }

    /**
     * Get trending posts based on views and recency
     */
    @Transactional(readOnly = true)
    public List<BlogPost> getTrendingPosts(int limit) {
        // Get posts from last 30 days with highest views
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        Pageable pageable = PageRequest.of(0, limit);
        return blogPostRepository.findTrendingPosts(thirtyDaysAgo, pageable);
    }

    /**
     * Get popular posts from current week
     */
    @Transactional(readOnly = true)
    public List<BlogPost> getWeeklyPopularPosts(int limit) {
        // Get posts from last 7 days with highest views
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, limit);
        return blogPostRepository.findWeeklyPopularPosts(sevenDaysAgo, pageable);
    }

    /**
     * Get all-time most viewed posts
     */
    @Transactional(readOnly = true)
    public List<BlogPost> getAllTimePopularPosts(int limit) {
        return blogPostRepository.findTop10ByPublishedTrueOrderByViewCountDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Count total published posts
     */
    @Transactional(readOnly = true)
    public Long countPublishedPosts() {
        return blogPostRepository.countByPublishedTrue();
    }
}
