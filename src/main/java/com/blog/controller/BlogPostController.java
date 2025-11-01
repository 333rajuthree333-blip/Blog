package com.blog.controller;

import com.blog.dto.AIGenerateRequest;
import com.blog.dto.ApiResponse;
import com.blog.dto.BlogPostRequest;
import com.blog.dto.CommentRequest;
import com.blog.dto.NewsletterSubscriptionRequest;
import com.blog.dto.PollCreateRequest;
import com.blog.dto.PollResponse;
import com.blog.dto.PollVoteRequest;
import com.blog.model.BlogComment;
import com.blog.model.BlogPoll;
import com.blog.model.BlogPost;
import com.blog.model.NewsletterSubscription;
import com.blog.model.PollVote;
import com.blog.service.BlogCommentService;
import com.blog.service.BlogPostService;
import com.blog.service.NewsletterService;
import com.blog.service.PollService;
import com.blog.service.SchemaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BlogPostController {

    private final BlogPostService blogPostService;
    private final PollService pollService;
    // private final SmartSearchService smartSearchService;
    private final BlogCommentService blogCommentService;
    private final NewsletterService newsletterService;
    private final SchemaService schemaService;

    /**
     * Get all published blog posts with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getAllPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "en") String lang) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<BlogPost> posts = blogPostService.getPublishedPosts(pageable);
            
            Page<Map<String, Object>> translatedPosts = posts.map(post -> {
                Map<String, Object> postData = new java.util.HashMap<>();
                postData.put("id", post.getId());
                postData.put("author", post.getAuthor());
                postData.put("createdAt", post.getCreatedAt());
                postData.put("updatedAt", post.getUpdatedAt());
                postData.put("published", post.getPublished());
                postData.put("publishedAt", post.getPublishedAt());
                postData.put("viewCount", post.getViewCount());
                postData.put("isAiGenerated", post.getIsAiGenerated());
                postData.put("aiPrompt", post.getAiPrompt());
                postData.put("tags", post.getTags());
                postData.put("imageUrls", post.getImageUrls());
                postData.put("featuredImage", post.getFeaturedImage());
                
                // Language-specific content
                switch (lang.toLowerCase()) {
                    case "bn":
                        postData.put("title", post.getTitleBn() != null ? post.getTitleBn() : post.getTitle());
                        postData.put("content", post.getContentBn() != null ? post.getContentBn() : post.getContent());
                        postData.put("excerpt", post.getExcerptBn() != null ? post.getExcerptBn() : post.getExcerpt());
                        break;
                    case "hi":
                        postData.put("title", post.getTitleHi() != null ? post.getTitleHi() : post.getTitle());
                        postData.put("content", post.getContentHi() != null ? post.getContentHi() : post.getContent());
                        postData.put("excerpt", post.getExcerptHi() != null ? post.getExcerptHi() : post.getExcerpt());
                        break;
                    default:
                        postData.put("title", post.getTitle());
                        postData.put("content", post.getContent());
                        postData.put("excerpt", post.getExcerpt());
                        break;
                }
                
                return postData;
            });
            
            return ResponseEntity.ok(ApiResponse.success(translatedPosts));
        } catch (Exception e) {
            log.error("Error fetching posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching posts: " + e.getMessage()));
        }
    }

    /**
     * Get all posts (including drafts) - Admin only
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<BlogPost>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<BlogPost> posts = blogPostService.getAllPosts(pageable);
            return ResponseEntity.ok(ApiResponse.success(posts));
        } catch (Exception e) {
            log.error("Error fetching all posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching posts: " + e.getMessage()));
        }
    }

    /**
     * Get blog post by ID with schema markup
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPostById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "en") String lang) {
        try {
            BlogPost post = blogPostService.getPostById(id);
            blogPostService.incrementViewCount(id);

            // Prepare response data
            Map<String, Object> responseData = new java.util.HashMap<>();

            // Basic post data
            responseData.put("id", post.getId());
            responseData.put("author", post.getAuthor());
            responseData.put("createdAt", post.getCreatedAt());
            responseData.put("updatedAt", post.getUpdatedAt());
            responseData.put("published", post.getPublished());
            responseData.put("publishedAt", post.getPublishedAt());
            responseData.put("viewCount", post.getViewCount());
            responseData.put("isAiGenerated", post.getIsAiGenerated());
            responseData.put("aiPrompt", post.getAiPrompt());
            responseData.put("tags", post.getTags());
            responseData.put("imageUrls", post.getImageUrls());
            responseData.put("featuredImage", post.getFeaturedImage());

            // Language-specific content
            switch (lang.toLowerCase()) {
                case "bn":
                    responseData.put("title", post.getTitleBn() != null ? post.getTitleBn() : post.getTitle());
                    responseData.put("content", post.getContentBn() != null ? post.getContentBn() : post.getContent());
                    responseData.put("excerpt", post.getExcerptBn() != null ? post.getExcerptBn() : post.getExcerpt());
                    break;
                case "hi":
                    responseData.put("title", post.getTitleHi() != null ? post.getTitleHi() : post.getTitle());
                    responseData.put("content", post.getContentHi() != null ? post.getContentHi() : post.getContent());
                    responseData.put("excerpt", post.getExcerptHi() != null ? post.getExcerptHi() : post.getExcerpt());
                    break;
                default:
                    responseData.put("title", post.getTitle());
                    responseData.put("content", post.getContent());
                    responseData.put("excerpt", post.getExcerpt());
                    break;
            }

            // Generate schema markup
            String baseUrl = "https://techsci-blog.com"; // Replace with your actual domain
            String articleSchema = schemaService.generateArticleSchema(post, baseUrl);

            // Extract and generate additional schemas
            String content = post.getContent();
            List<Map<String, String>> faqs = schemaService.extractFAQsFromContent(content);
            String faqSchema = faqs.isEmpty() ? null : schemaService.generateFAQSchema(faqs);

            Map<String, Object> softwareInfo = schemaService.extractSoftwareInfo(content, post.getTitle());
            String softwareSchema = softwareInfo.isEmpty() ? null : schemaService.generateSoftwareSchema(softwareInfo);

            // Add schemas to response
            Map<String, String> schemas = new java.util.HashMap<>();
            schemas.put("article", articleSchema);
            if (faqSchema != null) schemas.put("faq", faqSchema);
            if (softwareSchema != null) schemas.put("software", softwareSchema);

            responseData.put("schemas", schemas);

            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            log.error("Error fetching post {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Post not found: " + e.getMessage()));
        }
    }

    /**
     * Search blog posts
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BlogPost>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<BlogPost> posts = blogPostService.searchPosts(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(posts));
        } catch (Exception e) {
            log.error("Error searching posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error searching posts: " + e.getMessage()));
        }
    }

    /**
     * Get posts by tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponse<Page<BlogPost>>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<BlogPost> posts = blogPostService.getPostsByTag(tag, pageable);
            return ResponseEntity.ok(ApiResponse.success(posts));
        } catch (Exception e) {
            log.error("Error fetching posts by tag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching posts: " + e.getMessage()));
        }
    }

    /**
     * Get top posts by views
     */
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<BlogPost>>> getTopPosts() {
        try {
            List<BlogPost> posts = blogPostService.getTopPosts();
            return ResponseEntity.ok(ApiResponse.success(posts));
        } catch (Exception e) {
            log.error("Error fetching top posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching top posts: " + e.getMessage()));
        }
    }

    /**
     * Create a new blog post manually
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BlogPost>> createPost(@Valid @RequestBody BlogPostRequest request) {
        try {
            BlogPost post = new BlogPost();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setExcerpt(request.getExcerpt());
            post.setAuthor(request.getAuthor() != null ? request.getAuthor() : "Anonymous");
            post.setTags(request.getTags());
            post.setFeaturedImage(request.getFeaturedImage());
            post.setPublished(request.getPublished() != null ? request.getPublished() : false);
            
            BlogPost savedPost = blogPostService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Post created successfully", savedPost));
        } catch (Exception e) {
            log.error("Error creating post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating post: " + e.getMessage()));
        }
    }

    /**
     * Generate blog post using AI
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<BlogPost>> generatePost(@Valid @RequestBody AIGenerateRequest request) {
        try {
            log.info("Generating AI post with prompt: {}", request.getPrompt());
            BlogPost post = blogPostService.generateBlogPost(request.getPrompt(), request.getAuthor());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("AI-generated post created successfully", post));
        } catch (Exception e) {
            log.error("Error generating AI post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generating post: " + e.getMessage()));
        }
    }

    /**
     * Update blog post
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogPost>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request
    ) {
        try {
            BlogPost post = new BlogPost();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setExcerpt(request.getExcerpt());
            post.setAuthor(request.getAuthor());
            post.setTags(request.getTags());
            post.setFeaturedImage(request.getFeaturedImage());
            
            BlogPost updatedPost = blogPostService.updatePost(id, post);
            return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updatedPost));
        } catch (Exception e) {
            log.error("Error updating post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating post: " + e.getMessage()));
        }
    }

    /**
     * Toggle publish status
     */
    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<BlogPost>> togglePublish(@PathVariable Long id) {
        try {
            BlogPost post = blogPostService.togglePublish(id);
            String message = post.getPublished() ? "Post published successfully" : "Post unpublished successfully";
            return ResponseEntity.ok(ApiResponse.success(message, post));
        } catch (Exception e) {
            log.error("Error toggling publish status for post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating post: " + e.getMessage()));
        }
    }

    /**
     * Add image to post
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<BlogPost>> addImage(
            @PathVariable Long id,
            @RequestBody String imageUrl
    ) {
        try {
            BlogPost post = blogPostService.addImageToPost(id, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("Image added successfully", post));
        } catch (Exception e) {
            log.error("Error adding image to post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error adding image: " + e.getMessage()));
        }
    }

    /**
     * Set featured image
     */
    @PatchMapping("/{id}/featured-image")
    public ResponseEntity<ApiResponse<BlogPost>> setFeaturedImage(
            @PathVariable Long id,
            @RequestBody String imageUrl
    ) {
        try {
            BlogPost post = blogPostService.setFeaturedImage(id, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("Featured image set successfully", post));
        } catch (Exception e) {
            log.error("Error setting featured image for post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error setting featured image: " + e.getMessage()));
        }
    }

    /**
     * Delete blog post
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        try {
            blogPostService.deletePost(id);
            return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting post: " + e.getMessage()));
        }
    }

    /**
     * Get post statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<?>> getStats() {
        try {
            Long totalPosts = blogPostService.countPublishedPosts();
            return ResponseEntity.ok(ApiResponse.success(
                    java.util.Map.of("totalPublishedPosts", totalPosts)
            ));
        } catch (Exception e) {
            log.error("Error fetching stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching stats: " + e.getMessage()));
        }
    }
    @GetMapping("/sitemap.xml")
    public ResponseEntity<String> generateSitemap() {
        try {
            List<BlogPost> publishedPosts = blogPostService.getPublishedPosts(PageRequest.of(0, 1000)).getContent();

            StringBuilder sitemap = new StringBuilder();
            sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n");
            sitemap.append("        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            sitemap.append("        xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\n");
            sitemap.append("        http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n\n");

            // Homepage
            sitemap.append("    <!-- Homepage -->\n");
            sitemap.append("    <url>\n");
            sitemap.append("        <loc>https://techsci-blog.com/</loc>\n");
            sitemap.append("        <lastmod>").append(java.time.LocalDate.now()).append("</lastmod>\n");
            sitemap.append("        <changefreq>daily</changefreq>\n");
            sitemap.append("        <priority>1.0</priority>\n");
            sitemap.append("    </url>\n\n");

            // Static Pages
            String[] staticPages = {"about", "advertisement", "circulation", "terms", "contact", "newsletter", "report"};
            for (String page : staticPages) {
                sitemap.append("    <url>\n");
                sitemap.append("        <loc>https://techsci-blog.com/").append(page).append(".html</loc>\n");
                sitemap.append("        <lastmod>").append(java.time.LocalDate.now()).append("</lastmod>\n");
                sitemap.append("        <changefreq>monthly</changefreq>\n");
                sitemap.append("        <priority>").append(page.equals("about") || page.equals("contact") ? "0.8" : "0.7").append("</priority>\n");
                sitemap.append("    </url>\n\n");
            }

            // Dynamic Blog Posts
            sitemap.append("    <!-- Dynamic Blog Posts -->\n");
            for (BlogPost post : publishedPosts) {
                sitemap.append("    <url>\n");
                sitemap.append("        <loc>https://techsci-blog.com/post-detail.html?id=").append(post.getId()).append("</loc>\n");
                sitemap.append("        <lastmod>").append(post.getUpdatedAt().toLocalDate()).append("</lastmod>\n");
                sitemap.append("        <changefreq>weekly</changefreq>\n");
                sitemap.append("        <priority>0.9</priority>\n");
                sitemap.append("    </url>\n");
            }

            sitemap.append("</urlset>");

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_XML)
                    .body(sitemap.toString());

        } catch (Exception e) {
            log.error("Error generating sitemap", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating sitemap");
        }
    }

    /**
     * Get blog posts for chatbot knowledge base
     */
    @GetMapping("/chatbot/posts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getChatbotPosts() {
        try {
            List<BlogPost> posts = blogPostService.getPublishedPosts(PageRequest.of(0, 50, Sort.by("createdAt").descending())).getContent();

            List<Map<String, Object>> chatbotPosts = posts.stream().map(post -> {
                Map<String, Object> postData = new java.util.HashMap<>();
                postData.put("id", post.getId());
                postData.put("title", post.getTitle());
                postData.put("excerpt", post.getExcerpt());
                postData.put("tags", post.getTags());
                postData.put("createdAt", post.getCreatedAt());
                postData.put("url", "/post-detail.html?id=" + post.getId());
                return postData;
            }).collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(chatbotPosts));
        } catch (Exception e) {
            log.error("Error fetching posts for chatbot", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching posts: " + e.getMessage()));
        }
    }

    /**
     * Smart search with AI-powered suggestions and relevance scoring
     */
    /*
    @GetMapping("/search/smart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> smartSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Map<String, Object> searchResults = smartSearchService.smartSearch(query, page, size);
            return ResponseEntity.ok(ApiResponse.success(searchResults));
        } catch (Exception e) {
            log.error("Error in smart search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error performing smart search: " + e.getMessage()));
        }
    }
    */

    /**
     * AI-powered search with semantic understanding
     */
    /*
    @GetMapping("/search/ai")
    public ResponseEntity<ApiResponse<Map<String, Object>>> aiPoweredSearch(@RequestParam String query) {
        try {
            Map<String, Object> searchResults = smartSearchService.aiPoweredSearch(query);
            return ResponseEntity.ok(ApiResponse.success(searchResults));
        } catch (Exception e) {
            log.error("Error in AI-powered search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error performing AI search: " + e.getMessage()));
        }
    }
    */

    /**
     * Get comments for a specific post
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<BlogComment>>> getCommentsForPost(@PathVariable Long postId) {
        try {
            List<BlogComment> comments = blogCommentService.getCommentsForPost(postId);
            return ResponseEntity.ok(ApiResponse.success(comments));
        } catch (Exception e) {
            log.error("Error fetching comments for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching comments: " + e.getMessage()));
        }
    }

    /**
     * Get comment count for a post
     */
    @GetMapping("/{postId}/comments/count")
    public ResponseEntity<ApiResponse<Long>> getCommentCountForPost(@PathVariable Long postId) {
        try {
            Long count = blogCommentService.getCommentCountForPost(postId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            log.error("Error counting comments for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error counting comments: " + e.getMessage()));
        }
    }

    /**
     * Create a new comment
     */
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<BlogComment>> createComment(
            @Valid @RequestBody CommentRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Get client IP address
            String ipAddress = getClientIpAddress(httpRequest);

            BlogComment comment = blogCommentService.createComment(request, ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Comment created successfully", comment));
        } catch (Exception e) {
            log.error("Error creating comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating comment: " + e.getMessage()));
        }
    }

    /**
     * Upvote a comment
     */
    @PostMapping("/comments/{commentId}/upvote")
    public ResponseEntity<ApiResponse<BlogComment>> upvoteComment(@PathVariable Long commentId) {
        try {
            BlogComment comment = blogCommentService.upvoteComment(commentId);
            return ResponseEntity.ok(ApiResponse.success("Comment upvoted successfully", comment));
        } catch (Exception e) {
            log.error("Error upvoting comment {}", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error upvoting comment: " + e.getMessage()));
        }
    }

    /**
     * Delete a comment (admin only - should add authentication)
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        try {
            blogCommentService.deleteComment(commentId);
            return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting comment {}", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting comment: " + e.getMessage()));
        }
    }

    /**
     * Get trending posts (last 30 days, sorted by views)
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTrendingPosts(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<BlogPost> trendingPosts = blogPostService.getTrendingPosts(limit);
            List<Map<String, Object>> result = trendingPosts.stream().map(post -> {
                Map<String, Object> postData = new HashMap<>();
                postData.put("id", post.getId());
                postData.put("title", post.getTitle());
                postData.put("excerpt", post.getExcerpt());
                postData.put("viewCount", post.getViewCount());
                postData.put("createdAt", post.getCreatedAt());
                postData.put("url", "/post-detail.html?id=" + post.getId());
                return postData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Error fetching trending posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching trending posts: " + e.getMessage()));
        }
    }

    /**
     * Get weekly popular posts (last 7 days)
     */
    @GetMapping("/weekly-popular")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWeeklyPopularPosts(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<BlogPost> weeklyPosts = blogPostService.getWeeklyPopularPosts(limit);
            List<Map<String, Object>> result = weeklyPosts.stream().map(post -> {
                Map<String, Object> postData = new HashMap<>();
                postData.put("id", post.getId());
                postData.put("title", post.getTitle());
                postData.put("excerpt", post.getExcerpt());
                postData.put("viewCount", post.getViewCount());
                postData.put("createdAt", post.getCreatedAt());
                postData.put("url", "/post-detail.html?id=" + post.getId());
                return postData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Error fetching weekly popular posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching weekly popular posts: " + e.getMessage()));
        }
    }

    /**
     * Get all-time popular posts
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTimePopularPosts(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<BlogPost> popularPosts = blogPostService.getAllTimePopularPosts(limit);
            List<Map<String, Object>> result = popularPosts.stream().map(post -> {
                Map<String, Object> postData = new HashMap<>();
                postData.put("id", post.getId());
                postData.put("title", post.getTitle());
                postData.put("excerpt", post.getExcerpt());
                postData.put("viewCount", post.getViewCount());
                postData.put("createdAt", post.getCreatedAt());
                postData.put("url", "/post-detail.html?id=" + post.getId());
                return postData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Error fetching popular posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching popular posts: " + e.getMessage()));
        }
    }

    /**
     * Subscribe to newsletter
     */
    @PostMapping("/newsletter/subscribe")
    public ResponseEntity<ApiResponse<NewsletterSubscription>> subscribeToNewsletter(
            @Valid @RequestBody NewsletterSubscriptionRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            NewsletterSubscription subscription = newsletterService.subscribe(request, ipAddress, "api");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Successfully subscribed to newsletter! Please check your email to confirm.", subscription));
        } catch (Exception e) {
            log.error("Error subscribing to newsletter", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Subscription failed: " + e.getMessage()));
        }
    }

/**
 * Confirm newsletter subscription
 */
@GetMapping("/newsletter/confirm/{token}")
public ResponseEntity<ApiResponse<String>> confirmSubscription(@PathVariable String token) {
    try {
        newsletterService.confirmSubscription(token);
        return ResponseEntity.ok(ApiResponse.success("Email confirmed! You are now subscribed to our newsletter."));
    } catch (Exception e) {
        log.error("Error confirming subscription", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Confirmation failed: " + e.getMessage()));
    }
}

/**
 * Unsubscribe from newsletter
 */
@PostMapping("/newsletter/unsubscribe")
public ResponseEntity<ApiResponse<String>> unsubscribeFromNewsletter(@RequestParam String email) {
    try {
        newsletterService.unsubscribe(email);
        return ResponseEntity.ok(ApiResponse.success("Successfully unsubscribed from newsletter."));
    } catch (Exception e) {
        log.error("Error unsubscribing from newsletter", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Unsubscription failed: " + e.getMessage()));
    }
}

/**
 * Get all active polls
 */
@GetMapping("/polls")
public ResponseEntity<ApiResponse<List<PollResponse>>> getActivePolls() {
    try {
        List<PollResponse> polls = pollService.getActivePolls();
        return ResponseEntity.ok(ApiResponse.success(polls));
    } catch (Exception e) {
        log.error("Error fetching active polls", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching polls: " + e.getMessage()));
    }
}

/**
 * Get a specific poll by ID
 */
@GetMapping("/polls/{pollId}")
public ResponseEntity<ApiResponse<PollResponse>> getPollById(
        @PathVariable Long pollId,
        @RequestParam(required = false) String voterId) {
    try {
        PollResponse poll = pollService.getPollById(pollId, voterId);
        return ResponseEntity.ok(ApiResponse.success(poll));
    } catch (Exception e) {
        log.error("Error fetching poll {}", pollId, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Poll not found: " + e.getMessage()));
    }
}

/**
 * Vote on a poll
 */
@PostMapping("/polls/vote")
public ResponseEntity<ApiResponse<PollResponse>> voteOnPoll(
        @Valid @RequestBody PollVoteRequest request,
        HttpServletRequest httpRequest) {
    try {
        // Generate voter identifier if not provided
        String voterIdentifier = request.getVoterIdentifier();
        if (voterIdentifier == null || voterIdentifier.isEmpty()) {
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            voterIdentifier = PollVote.generateVoterId(ipAddress, userAgent != null ? userAgent : "");
        }

        PollResponse updatedPoll = pollService.voteOnPoll(
                request.getOptionId(),
                voterIdentifier,
                getClientIpAddress(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        return ResponseEntity.ok(ApiResponse.success("Vote recorded successfully", updatedPoll));
    } catch (Exception e) {
        log.error("Error recording vote", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error recording vote: " + e.getMessage()));
    }
}

/**
 * Create a new poll (admin only - should add authentication)
 */
@PostMapping("/polls")
public ResponseEntity<ApiResponse<BlogPoll>> createPoll(@Valid @RequestBody PollCreateRequest request) {
    try {
        BlogPoll poll = pollService.createPoll(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Poll created successfully", poll));
    } catch (Exception e) {
        log.error("Error creating poll", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error creating poll: " + e.getMessage()));
    }
}

/**
 * Delete a poll (admin only)
 */
@DeleteMapping("/polls/{pollId}")
public ResponseEntity<ApiResponse<Void>> deletePoll(@PathVariable Long pollId) {
    try {
        pollService.deletePoll(pollId);
        return ResponseEntity.ok(ApiResponse.success("Poll deleted successfully", null));
    } catch (Exception e) {
        log.error("Error deleting poll {}", pollId, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error deleting poll: " + e.getMessage()));
    }
}

/**
 * Toggle poll active status (admin only)
 */
@PatchMapping("/polls/{pollId}/toggle")
public ResponseEntity<ApiResponse<BlogPoll>> togglePollStatus(@PathVariable Long pollId) {
    try {
        BlogPoll poll = pollService.togglePollStatus(pollId);
        String message = poll.getActive() ? "Poll activated" : "Poll deactivated";
        return ResponseEntity.ok(ApiResponse.success(message, poll));
    } catch (Exception e) {
        log.error("Error toggling poll status {}", pollId, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error updating poll: " + e.getMessage()));
    }
}

    /**
     * Helper method to get client IP address from HttpServletRequest
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP if there are multiple
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        String xForwarded = request.getHeader("X-Forwarded");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded;
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }

}
