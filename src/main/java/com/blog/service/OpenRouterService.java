package com.blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public OpenRouterService(@Value("${openrouter.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String generateBlogPostContent(String topic) {
        String prompt = "Write a comprehensive and engaging blog post about: " + topic + 
                       ". Make it informative, well-structured with headings, and suitable for a general audience.";
        return generateResponse(prompt);
    }

    public String generateBlogPostContentBn(String topic) {
        String prompt = "Write a comprehensive and engaging blog post in Bengali about: " + topic + 
                       ". Make it informative, well-structured with headings, and suitable for a general Bengali audience. Respond in Bengali language only.";
        return generateResponse(prompt);
    }

    public String generateBlogPostContentHi(String topic) {
        String prompt = "Write a comprehensive and engaging blog post in Hindi about: " + topic + 
                       ". Make it informative, well-structured with headings, and suitable for a general Hindi audience. Respond in Hindi language only.";
        return generateResponse(prompt);
    }

    public String generateTitle(String content) {
        String prompt = "Generate a concise, attractive, and SEO-friendly title (3-200 characters) for this blog post content. Respond with only the title, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateTitleBn(String content) {
        String prompt = "Generate a concise, attractive title in Bengali (3-200 characters) for this blog post content. Respond with only the title in Bengali, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateTitleHi(String content) {
        String prompt = "Generate a concise, attractive title in Hindi (3-200 characters) for this blog post content. Respond with only the title in Hindi, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateExcerpt(String content) {
        String prompt = "Create a compelling 2-3 sentence excerpt/summary for this blog post. Respond with only the excerpt, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateExcerptBn(String content) {
        String prompt = "Create a compelling 2-3 sentence excerpt/summary in Bengali for this blog post. Respond with only the excerpt in Bengali, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateExcerptHi(String content) {
        String prompt = "Create a compelling 2-3 sentence excerpt/summary in Hindi for this blog post. Respond with only the excerpt in Hindi, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateTags(String content) {
        String prompt = "Generate 5-8 relevant tags/keywords for this blog post, separated by commas. Respond with only the tags, nothing else: " + 
                       content.substring(0, Math.min(500, content.length()));
        return generateResponse(prompt).trim();
    }

    public String generateResponse(String prompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://your-blog-site.com");
        headers.set("X-Title", "Blog Website");
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek/deepseek-chat",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        Map<String, Object> choice = choices.get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choice.get("message");
        return (String) message.get("content");
    }
}
