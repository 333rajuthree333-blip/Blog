package com.blog.service;

import com.blog.model.BlogPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchemaService {

    private final ObjectMapper objectMapper;

    /**
     * Generate Article schema for blog posts
     */
    public String generateArticleSchema(BlogPost post, String baseUrl) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "Article");

        // Basic article properties
        schema.put("headline", post.getTitle());
        schema.put("description", post.getExcerpt() != null ? post.getExcerpt() : post.getContent().substring(0, 160));

        // Images
        if (post.getFeaturedImage() != null) {
            ArrayNode images = schema.putArray("image");
            images.add(baseUrl + post.getFeaturedImage());
        }

        // Author
        ObjectNode author = schema.putObject("author");
        author.put("@type", "Person");
        author.put("name", post.getAuthor() != null ? post.getAuthor() : "TechSci Blog");

        // Publisher
        ObjectNode publisher = schema.putObject("publisher");
        publisher.put("@type", "Organization");
        publisher.put("name", "TechSci Blog");
        publisher.put("url", baseUrl);

        ObjectNode logo = publisher.putObject("logo");
        logo.put("@type", "ImageObject");
        logo.put("url", baseUrl + "/logo.svg");

        // Dates
        if (post.getPublishedAt() != null) {
            schema.put("datePublished", post.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (post.getUpdatedAt() != null) {
            schema.put("dateModified", post.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        // Article metadata
        schema.set("mainEntityOfPage", objectMapper.valueToTree(Map.of("@type", "WebPage", "@id", baseUrl + "/post-detail.html?id=" + post.getId())));
        schema.put("wordCount", post.getContent() != null ? post.getContent().split("\\s+").length : 0);
        schema.put("articleSection", "Technology");
        schema.put("keywords", post.getTags() != null ? String.join(", ", post.getTags()) : "");

        return schema.toString();
    }

    /**
     * Generate FAQ schema for posts with Q&A content
     */
    public String generateFAQSchema(List<Map<String, String>> faqs) {
        if (faqs == null || faqs.isEmpty()) return null;

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "FAQPage");

        ArrayNode mainEntity = schema.putArray("mainEntity");

        for (Map<String, String> faq : faqs) {
            ObjectNode question = mainEntity.addObject();
            question.put("@type", "Question");
            question.put("name", faq.get("question"));

            ObjectNode answer = question.putObject("acceptedAnswer");
            answer.put("@type", "Answer");
            answer.put("text", faq.get("answer"));
        }

        return schema.toString();
    }

    /**
     * Generate SoftwareApplication schema for tool reviews
     */
    public String generateSoftwareSchema(Map<String, Object> softwareInfo) {
        if (softwareInfo == null || softwareInfo.isEmpty()) return null;

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "SoftwareApplication");

        schema.put("name", (String) softwareInfo.getOrDefault("name", ""));
        schema.put("description", (String) softwareInfo.getOrDefault("description", ""));

        if (softwareInfo.containsKey("rating")) {
            ObjectNode aggregateRating = schema.putObject("aggregateRating");
            aggregateRating.put("@type", "AggregateRating");
            aggregateRating.put("ratingValue", ((Number) softwareInfo.get("rating")).doubleValue());
            aggregateRating.put("ratingCount", ((Number) softwareInfo.getOrDefault("ratingCount", 0)).intValue());
        }

        if (softwareInfo.containsKey("price")) {
            ObjectNode offers = schema.putObject("offers");
            offers.put("@type", "Offer");
            offers.put("price", softwareInfo.get("price").toString());
            offers.put("priceCurrency", (String) softwareInfo.getOrDefault("currency", "USD"));
        }

        schema.put("operatingSystem", (String) softwareInfo.getOrDefault("operatingSystem", "Web"));
        schema.put("applicationCategory", (String) softwareInfo.getOrDefault("category", "Productivity"));

        return schema.toString();
    }

    /**
     * Generate Review/Rating schema
     */
    public String generateReviewSchema(Map<String, Object> reviewInfo) {
        if (reviewInfo == null || reviewInfo.isEmpty()) return null;

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "Review");

        if (reviewInfo.containsKey("itemReviewed")) {
            ObjectNode itemReviewed = schema.putObject("itemReviewed");
            itemReviewed.put("@type", "SoftwareApplication");
            itemReviewed.put("name", (String) reviewInfo.get("itemReviewed"));
        }

        ObjectNode reviewRating = schema.putObject("reviewRating");
        reviewRating.put("@type", "Rating");
        reviewRating.put("ratingValue", ((Number) reviewInfo.getOrDefault("rating", 5)).doubleValue());
        reviewRating.put("bestRating", ((Number) reviewInfo.getOrDefault("bestRating", 5)).intValue());

        schema.set("author", objectMapper.valueToTree(Map.of("@type", "Person", "name", reviewInfo.getOrDefault("author", "Anonymous"))));
        schema.put("reviewBody", (String) reviewInfo.getOrDefault("reviewBody", ""));

        return schema.toString();
    }

    /**
     * Generate Organization schema for the blog
     */
    public String generateOrganizationSchema(String baseUrl) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "Organization");

        schema.put("name", "TechSci Blog");
        schema.put("url", baseUrl);
        schema.put("description", "Your ultimate source for technology and science news, tutorials, and insights");

        ArrayNode sameAs = schema.putArray("sameAs");
        sameAs.add("https://twitter.com/techsci-blog");
        sameAs.add("https://facebook.com/techsci-blog");
        sameAs.add("https://linkedin.com/company/techsci-blog");

        ObjectNode logo = schema.putObject("logo");
        logo.put("@type", "ImageObject");
        logo.put("url", baseUrl + "/logo.svg");

        return schema.toString();
    }

    /**
     * Generate Website schema
     */
    public String generateWebsiteSchema(String baseUrl) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "WebSite");

        schema.put("name", "TechSci Blog");
        schema.put("url", baseUrl);
        schema.put("description", "Latest technology and science news, AI tools, programming tutorials");

        ObjectNode publisher = schema.putObject("publisher");
        publisher.put("@type", "Organization");
        publisher.put("name", "TechSci Blog");

        ObjectNode potentialAction = schema.putObject("potentialAction");
        potentialAction.put("@type", "SearchAction");
        potentialAction.set("target", objectMapper.valueToTree(Map.of("@type", "EntryPoint", "urlTemplate", baseUrl + "/search?q={search_term_string}")));

        ObjectNode queryInput = potentialAction.putObject("query-input");
        queryInput.put("@type", "PropertyValueSpecification");
        queryInput.put("valueRequired", true);
        queryInput.put("valueName", "search_term_string");

        return schema.toString();
    }

    /**
     * Generate BreadcrumbList schema
     */
    public String generateBreadcrumbSchema(List<Map<String, String>> breadcrumbs, String baseUrl) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "BreadcrumbList");

        ArrayNode itemListElement = schema.putArray("itemListElement");

        for (int i = 0; i < breadcrumbs.size(); i++) {
            Map<String, String> crumb = breadcrumbs.get(i);
            ObjectNode item = itemListElement.addObject();
            item.put("@type", "ListItem");
            item.put("position", i + 1);

            ObjectNode itemNode = item.putObject("item");
            itemNode.put("@type", "WebPage");
            itemNode.put("name", crumb.get("name"));
            itemNode.put("url", baseUrl + crumb.get("url"));
        }

        return schema.toString();
    }

    /**
     * Extract FAQs from post content (basic implementation)
     */
    public List<Map<String, String>> extractFAQsFromContent(String content) {
        // This is a basic implementation - in production, you might use AI or manual tagging
        List<Map<String, String>> faqs = new java.util.ArrayList<>();

        if (content == null || content.isEmpty()) return faqs;

        // Look for common FAQ patterns
        String[] lines = content.split("\n");
        String currentQuestion = null;

        for (String line : lines) {
            line = line.trim();
            if (line.toLowerCase().startsWith("q:") || line.toLowerCase().startsWith("question:")) {
                currentQuestion = line.substring(line.indexOf(":") + 1).trim();
            } else if (currentQuestion != null && (line.toLowerCase().startsWith("a:") || line.toLowerCase().startsWith("answer:"))) {
                String answer = line.substring(line.indexOf(":") + 1).trim();
                faqs.add(Map.of("question", currentQuestion, "answer", answer));
                currentQuestion = null;
            }
        }

        return faqs;
    }

    /**
     * Extract tool/software info from post content
     */
    public Map<String, Object> extractSoftwareInfo(String content, String title) {
        Map<String, Object> info = new java.util.HashMap<>();

        if (content == null || content.isEmpty()) return info;

        // Extract tool name from title or content
        info.put("name", title.replaceAll("Review|Tutorial|Guide|\\s+", " ").trim());

        // Look for rating patterns
        java.util.regex.Pattern ratingPattern = java.util.regex.Pattern.compile("(\\d+(\\.\\d+)?)/5|rating:\\s*(\\d+(\\.\\d+)?)");
        java.util.regex.Matcher matcher = ratingPattern.matcher(content.toLowerCase());
        if (matcher.find()) {
            try {
                double rating = Double.parseDouble(matcher.group(1) != null ? matcher.group(1) : matcher.group(3));
                info.put("rating", Math.min(rating, 5.0));
                info.put("ratingCount", 1); // In production, aggregate from multiple reviews
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Look for price patterns
        java.util.regex.Pattern pricePattern = java.util.regex.Pattern.compile("\\$(\\d+(?:\\.\\d{2})?)|price:\\s*\\$?(\\d+(?:\\.\\d{2})?)");
        matcher = pricePattern.matcher(content.toLowerCase());
        if (matcher.find()) {
            try {
                String priceStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                double price = Double.parseDouble(priceStr);
                info.put("price", price);
                info.put("currency", "USD");
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        info.put("operatingSystem", "Web");
        info.put("applicationCategory", "Productivity");

        return info;
    }
}
