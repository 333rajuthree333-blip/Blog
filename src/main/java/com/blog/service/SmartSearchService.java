package com.blog.service;

import com.blog.model.BlogPost;
import com.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartSearchService {

    private final BlogPostRepository blogPostRepository;
    private final ChatbotOpenRouterService chatbotOpenRouterService;

    /**
     * Enhanced search with AI-powered suggestions and relevance scoring
     */
    public Map<String, Object> smartSearch(String query, int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // Clean and prepare the search query
        String cleanQuery = query.trim().toLowerCase();

        // Get basic search results
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogPost> basicResults = blogPostRepository.searchByKeyword(cleanQuery, pageable);

        // Generate AI-powered search suggestions
        List<String> searchSuggestions = generateSearchSuggestions(cleanQuery);

        // Calculate relevance scores for results
        List<Map<String, Object>> scoredResults = scoreResults(basicResults.getContent(), cleanQuery);

        // Generate related topics
        List<String> relatedTopics = generateRelatedTopics(cleanQuery);

        // Build response
        result.put("query", query);
        result.put("totalResults", basicResults.getTotalElements());
        result.put("currentPage", page);
        result.put("totalPages", basicResults.getTotalPages());
        result.put("results", scoredResults);
        result.put("suggestions", searchSuggestions);
        result.put("relatedTopics", relatedTopics);
        result.put("searchTips", generateSearchTips(cleanQuery));

        return result;
    }

    /**
     * Generate AI-powered search suggestions
     */
    private List<String> generateSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();

        // Basic keyword expansion
        Map<String, List<String>> keywordMap = new HashMap<>();
        keywordMap.put("ai", Arrays.asList("artificial intelligence", "machine learning", "neural networks", "deep learning"));
        keywordMap.put("quantum", Arrays.asList("quantum computing", "quantum physics", "quantum mechanics"));
        keywordMap.put("space", Arrays.asList("space exploration", "nasa", "mars mission", "moon landing"));
        keywordMap.put("programming", Arrays.asList("coding", "software development", "web development", "mobile apps"));
        keywordMap.put("python", Arrays.asList("python programming", "django", "flask", "data science"));
        keywordMap.put("javascript", Arrays.asList("js", "react", "node.js", "frontend development"));

        for (Map.Entry<String, List<String>> entry : keywordMap.entrySet()) {
            if (query.contains(entry.getKey())) {
                suggestions.addAll(entry.getValue().stream()
                    .filter(s -> !query.contains(s))
                    .limit(2)
                    .collect(Collectors.toList()));
            }
        }

        // Add popular search terms if no matches
        if (suggestions.isEmpty()) {
            suggestions.addAll(Arrays.asList(
                "artificial intelligence",
                "machine learning",
                "quantum computing",
                "space exploration",
                "programming tutorials"
            ));
        }

        return suggestions.stream().distinct().limit(5).collect(Collectors.toList());
    }

    /**
     * Score search results by relevance
     */
    private List<Map<String, Object>> scoreResults(List<BlogPost> posts, String query) {
        return posts.stream().map(post -> {
            Map<String, Object> result = new HashMap<>();
            double score = calculateRelevanceScore(post, query);

            result.put("id", post.getId());
            result.put("title", post.getTitle());
            result.put("excerpt", post.getExcerpt());
            result.put("author", post.getAuthor());
            result.put("createdAt", post.getCreatedAt());
            result.put("viewCount", post.getViewCount());
            result.put("tags", post.getTags());
            result.put("score", score);
            result.put("url", "/post-detail.html?id=" + post.getId());

            return result;
        })
        .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
        .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for a post
     */
    private double calculateRelevanceScore(BlogPost post, String query) {
        double score = 0.0;
        String lowerQuery = query.toLowerCase();

        // Title matches (highest weight)
        if (post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerQuery)) {
            score += 10.0;
        }
        if (post.getTitleBn() != null && post.getTitleBn().toLowerCase().contains(lowerQuery)) {
            score += 9.0;
        }
        if (post.getTitleHi() != null && post.getTitleHi().toLowerCase().contains(lowerQuery)) {
            score += 9.0;
        }

        // Content matches
        if (post.getContent() != null && post.getContent().toLowerCase().contains(lowerQuery)) {
            score += 5.0;
        }
        if (post.getContentBn() != null && post.getContentBn().toLowerCase().contains(lowerQuery)) {
            score += 4.5;
        }
        if (post.getContentHi() != null && post.getContentHi().toLowerCase().contains(lowerQuery)) {
            score += 4.5;
        }

        // Excerpt matches
        if (post.getExcerpt() != null && post.getExcerpt().toLowerCase().contains(lowerQuery)) {
            score += 3.0;
        }

        // Tag matches
        if (post.getTags() != null) {
            for (String tag : post.getTags()) {
                if (tag.toLowerCase().contains(lowerQuery)) {
                    score += 7.0;
                    break;
                }
            }
        }

        // Recency bonus (newer posts get slight boost)
        if (post.getCreatedAt() != null) {
            long daysSinceCreated = java.time.temporal.ChronoUnit.DAYS.between(
                post.getCreatedAt().toLocalDate(),
                java.time.LocalDate.now()
            );
            if (daysSinceCreated < 30) {
                score += 2.0; // Boost recent posts
            } else if (daysSinceCreated < 90) {
                score += 1.0;
            }
        }

        // Popularity bonus
        if (post.getViewCount() != null && post.getViewCount() > 100) {
            score += Math.log(post.getViewCount()) * 0.5;
        }

        return score;
    }

    /**
     * Generate related topics
     */
    private List<String> generateRelatedTopics(String query) {
        Map<String, List<String>> topicMap = new HashMap<>();
        topicMap.put("ai", Arrays.asList("Machine Learning", "Neural Networks", "Deep Learning", "AI Ethics"));
        topicMap.put("quantum", Arrays.asList("Quantum Physics", "Quantum Algorithms", "Quantum Cryptography"));
        topicMap.put("space", Arrays.asList("Mars Exploration", "Moon Missions", "Space Tourism", "NASA"));
        topicMap.put("programming", Arrays.asList("Web Development", "Mobile Apps", "Software Engineering"));
        topicMap.put("python", Arrays.asList("Data Science", "Django", "Flask", "Automation"));
        topicMap.put("javascript", Arrays.asList("React", "Node.js", "Frontend Development", "Web Frameworks"));

        for (Map.Entry<String, List<String>> entry : topicMap.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return Arrays.asList("Technology", "Science", "Programming", "Innovation", "Research");
    }

    /**
     * Generate search tips
     */
    private List<String> generateSearchTips(String query) {
        List<String> tips = new ArrayList<>();

        if (query.length() < 3) {
            tips.add("Try using more specific keywords for better results");
        }

        tips.add("Search in English, Bengali (বাংলা), or Hindi (हिंदी)");
        tips.add("Use tags to find related content");
        tips.add("Try synonyms for broader results");

        return tips;
    }

    /**
     * Advanced AI-powered search with semantic understanding
     */
    public Map<String, Object> aiPoweredSearch(String query) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Use AI to understand the search intent and provide better results
            String aiPrompt = "Analyze this search query and suggest better search terms or related topics: '" + query + "'. " +
                            "Consider the context of technology and science blog. Provide 3-5 improved search suggestions.";

            String aiResponse = chatbotOpenRouterService.generateResponse(aiPrompt);
            result.put("aiSuggestions", aiResponse);

            // Still provide basic search results
            Pageable pageable = PageRequest.of(0, 10);
            Page<BlogPost> searchResults = blogPostRepository.searchByKeyword(query, pageable);

            List<Map<String, Object>> results = searchResults.getContent().stream().map(post -> {
                Map<String, Object> postData = new HashMap<>();
                postData.put("id", post.getId());
                postData.put("title", post.getTitle());
                postData.put("excerpt", post.getExcerpt());
                postData.put("url", "/post-detail.html?id=" + post.getId());
                return postData;
            }).collect(Collectors.toList());

            result.put("results", results);
            result.put("totalResults", searchResults.getTotalElements());

        } catch (Exception e) {
            log.error("Error in AI-powered search", e);
            // Fallback to regular search
            result.put("results", smartSearch(query, 0, 10));
        }

        return result;
    }
}
