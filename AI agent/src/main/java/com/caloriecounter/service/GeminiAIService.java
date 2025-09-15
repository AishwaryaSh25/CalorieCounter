package com.caloriecounter.service;

import com.caloriecounter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Gemini AI implementation of AIService.
 * Follows Dependency Inversion Principle (DIP) by implementing the AIService interface.
 * Follows Single Responsibility Principle (SRP) by delegating to specialized services.
 */
@Service
public class GeminiAIService implements AIService {
    
    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;
    
    @Autowired
    public GeminiAIService(GeminiService geminiService, PromptBuilder promptBuilder) {
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
    }
    
    @Override
    public String analyzeFood(User user, String foodName, double portionSize) {
        System.out.println("DEBUG: Using Gemini AI for food analysis: " + foodName);

        // Build the prompt using the dedicated prompt builder
        String prompt = promptBuilder.buildNutritionPrompt(user, foodName, portionSize);

        // Delegate to Gemini service for API call with user's API key
        return geminiService.callGeminiAPI(prompt, user.getGeminiApiKey());
    }
}
