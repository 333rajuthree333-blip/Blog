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
import java.util.Arrays;

@Service
public class ChatbotOpenRouterService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    // Sample blog posts for reference (in a real implementation, this would come from database)
    @SuppressWarnings("unused")
    private final List<Map<String, String>> sampleBlogPosts = Arrays.asList(
        Map.of("title", "The Future of Artificial Intelligence", "url", "/post-detail.html?id=1", "tags", "AI, Machine Learning, Technology"),
        Map.of("title", "Quantum Computing Explained", "url", "/post-detail.html?id=2", "tags", "Quantum Computing, Science, Technology"),
        Map.of("title", "Space Exploration: Journey to Mars", "url", "/post-detail.html?id=3", "tags", "Space Exploration, Mars, NASA"),
        Map.of("title", "Neural Networks and Deep Learning", "url", "/post-detail.html?id=4", "tags", "AI, Neural Networks, Deep Learning"),
        Map.of("title", "Programming with Python", "url", "/post-detail.html?id=5", "tags", "Python, Programming, Development"),
        Map.of("title", "JavaScript Modern Development", "url", "/post-detail.html?id=6", "tags", "JavaScript, Web Development, Programming"),
        Map.of("title", "Moon Missions and Lunar Exploration", "url", "/post-detail.html?id=7", "tags", "Moon, Space Exploration, NASA"),
        Map.of("title", "Machine Learning Algorithms", "url", "/post-detail.html?id=8", "tags", "Machine Learning, AI, Algorithms")
    );

    public ChatbotOpenRouterService(@Value("${chatbot.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String generateChatbotResponse(String message) {
        String baseResponse = generateAIResponse(message);
        String blogSuggestion = findRelevantBlogPost(message);

        if (blogSuggestion != null && !blogSuggestion.isEmpty()) {
            return baseResponse + "\n\n" + blogSuggestion;
        }

        return baseResponse;
    }

    private String generateAIResponse(String message) {
        String context = "You are a helpful AI assistant for a Technology & Science blog called 'TechSci'. The user is asking: \"" + message + "\"\n\n" +
                        "Please provide a helpful, accurate, and engaging response. Keep your response concise but informative. " +
                        "If the question is about technology or science, provide relevant insights. Be friendly and conversational.\n\n" +
                        "Current page context: This is a technology and science blog with articles about AI, quantum computing, space exploration, programming, and scientific discoveries.";

        return generateResponse(context);
    }

    private String findRelevantBlogPost(String message) {
        String lowerMessage = message.toLowerCase();

        // Keywords mapping to blog posts
        if (lowerMessage.contains("ai") || lowerMessage.contains("artificial intelligence") || lowerMessage.contains("machine learning")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [The Future of Artificial Intelligence](/post-detail.html?id=1)";
        }

        if (lowerMessage.contains("quantum") || lowerMessage.contains("quantum computing")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Quantum Computing Explained](/post-detail.html?id=2)";
        }

        if (lowerMessage.contains("space") || lowerMessage.contains("mars") || lowerMessage.contains("nasa") || lowerMessage.contains("space exploration")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Space Exploration: Journey to Mars](/post-detail.html?id=3)";
        }

        if (lowerMessage.contains("neural network") || lowerMessage.contains("deep learning")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Neural Networks and Deep Learning](/post-detail.html?id=4)";
        }

        if (lowerMessage.contains("python") || lowerMessage.contains("programming")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Programming with Python](/post-detail.html?id=5)";
        }

        if (lowerMessage.contains("javascript") || lowerMessage.contains("js") || lowerMessage.contains("web development")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [JavaScript Modern Development](/post-detail.html?id=6)";
        }

        if (lowerMessage.contains("moon") || lowerMessage.contains("lunar")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Moon Missions and Lunar Exploration](/post-detail.html?id=7)";
        }

        if (lowerMessage.contains("algorithm") || lowerMessage.contains("ml")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [Machine Learning Algorithms](/post-detail.html?id=8)";
        }

        // For general tech/science questions, suggest a random relevant post
        if (lowerMessage.contains("technology") || lowerMessage.contains("science") || lowerMessage.contains("tech")) {
            return "এই বিষয় নিয়ে বিস্তারিত পড়তে এই ব্লগটা দেখো 👉 [The Future of Artificial Intelligence](/post-detail.html?id=1)";
        }

        return null; // No relevant post found
    }

    public String generateResponse(String prompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://your-blog-site.com");
        headers.set("X-Title", "Blog Website Chatbot");
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
