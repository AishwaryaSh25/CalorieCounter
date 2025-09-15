package com.caloriecounter.service;

import com.caloriecounter.dto.FoodRecommendation;
import com.caloriecounter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HealthAnalysisService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AIService aiService; // Injected via interface - follows DIP

    @Autowired
    private AIResponseParser responseParser; // Follows SRP - dedicated response parsing
    
    public FoodRecommendation analyzeFoodForUser(Long userId, String foodName, Double portionSizeInGrams) {
        try {
            // Get user information
            User user = userService.findById(userId);
            
            // Get AI analysis directly with food name and portion
            String aiAnalysis = aiService.analyzeFood(user, foodName, portionSizeInGrams);
            
            // Create recommendation from AI analysis
            FoodRecommendation recommendation = new FoodRecommendation();
            recommendation.setFoodName(foodName);
            recommendation.setPortionSize(portionSizeInGrams);
            
            // Parse AI response using dedicated parser - follows SRP
            responseParser.parseAIResponse(aiAnalysis, recommendation, user);
            
            System.out.println("AI Analysis: " + aiAnalysis); // Debug log
            return recommendation;
            
        } catch (Exception e) {
            System.err.println("Error in food analysis: " + e.getMessage());
            throw new RuntimeException("Failed to analyze food: " + e.getMessage(), e);
        }
    }
    
}
