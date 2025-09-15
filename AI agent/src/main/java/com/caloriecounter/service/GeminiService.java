package com.caloriecounter.service;

import com.caloriecounter.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private long lastCallTime = 0;
    private static final long MIN_CALL_INTERVAL = 1000; // 1 second between calls (Gemini has better limits)
    
    // API key is now provided per user, not from properties

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.model}")
    private String model;
    
    public GeminiService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Makes a direct API call to Gemini with the provided prompt and user's API key.
     * Follows Single Responsibility Principle - only handles API communication.
     *
     * @param prompt The formatted prompt to send to Gemini
     * @param userApiKey The user's personal Gemini API key
     * @return The AI response as a string
     */
    public String callGeminiAPI(String prompt, String userApiKey) {
        System.out.println("DEBUG: User Gemini API key present: " + (userApiKey != null && !userApiKey.trim().isEmpty()));

        if (userApiKey == null || userApiKey.trim().isEmpty()) {
            throw new RuntimeException("Gemini API key not provided. Please enter your API key during registration.");
        }

        // Validate Gemini API key format
        if (!userApiKey.startsWith("AIza")) {
            throw new RuntimeException("Invalid Gemini API key format. Gemini API keys should start with 'AIza'. Get one from https://makersuite.google.com/app/apikey");
        }

        // Log the API key format for debugging (first 10 characters only)
        System.out.println("DEBUG: API key format: " + userApiKey.substring(0, Math.min(10, userApiKey.length())) + "...");

        return callGeminiWithRetry(prompt, userApiKey, 3);
    }
    
    private String callGeminiWithRetry(String prompt, String userApiKey, int maxRetries) {
        // Rate limiting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCallTime < MIN_CALL_INTERVAL) {
            long waitTime = MIN_CALL_INTERVAL - (currentTime - lastCallTime);
            System.out.println("DEBUG: Rate limiting - waiting " + waitTime + "ms");
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastCallTime = System.currentTimeMillis();
        
        // Prompt is now passed in, no need to build it here
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            System.out.println("DEBUG: Calling Gemini API (attempt " + attempt + "/" + maxRetries + ")...");
            
            try {
                // Build Gemini request body according to API spec
                Map<String, Object> requestBody = new HashMap<>();

                // Create parts array with text
                Map<String, Object> textPart = new HashMap<>();
                textPart.put("text", prompt);

                // Create contents array
                Map<String, Object> content = new HashMap<>();
                content.put("parts", List.of(textPart));

                requestBody.put("contents", List.of(content));

                System.out.println("DEBUG: Gemini request body: " + requestBody);

                String response = webClient.post()
                        .uri(apiUrl)
                        .header("Content-Type", "application/json")
                        .header("X-goog-api-key", userApiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                JsonNode jsonResponse = objectMapper.readTree(response);
                String result = jsonResponse.get("candidates").get(0)
                        .get("content").get("parts").get(0).get("text").asText();
                
                System.out.println("DEBUG: Gemini API call successful, response length: " + result.length());
                return result;
                
            } catch (Exception e) {
                System.err.println("DEBUG: Gemini API call failed (attempt " + attempt + "): " + e.getMessage());
                
                // Handle rate limiting with retry
                if (e.getMessage().contains("429") || e.getMessage().contains("Too Many Requests")) {
                    if (attempt < maxRetries) {
                        long waitTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
                        System.out.println("DEBUG: Rate limited, waiting " + waitTime + "ms before retry...");
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Request interrupted", ie);
                        }
                        continue; // Retry
                    } else {
                        throw new RuntimeException("Gemini API rate limit exceeded after " + maxRetries + " attempts. Please wait and try again.", e);
                    }
                } else if (e.getMessage().contains("403") || e.getMessage().contains("API_KEY_INVALID")) {
                    throw new RuntimeException("Invalid Gemini API key. Please check your API key in application.properties.", e);
                } else if (e.getMessage().contains("quota")) {
                    throw new RuntimeException("Gemini API quota exceeded. Please check your Google Cloud account.", e);
                } else {
                    throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
                }
            }
        }
        
        throw new RuntimeException("Gemini API call failed after " + maxRetries + " attempts");
    }
}
