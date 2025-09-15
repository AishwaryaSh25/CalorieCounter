package com.caloriecounter.service;

import com.caloriecounter.dto.FoodRecommendation;
import com.caloriecounter.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing AI responses into structured recommendations.
 * Follows Single Responsibility Principle (SRP) - only responsible for response parsing.
 */
@Component
public class AIResponseParser {
    
    /**
     * Parses AI response text into a structured FoodRecommendation object.
     * 
     * @param response The raw AI response text
     * @param recommendation The recommendation object to populate
     * @param user The user for calculating daily calorie percentage
     */
    public void parseAIResponse(String response, FoodRecommendation recommendation, User user) {
        try {
            System.out.println("DEBUG: Parsing AI response: " + (response != null ? response.substring(0, Math.min(200, response.length())) : "null"));
            
            if (response == null || response.trim().isEmpty()) {
                System.out.println("DEBUG: AI response is null or empty");
                throw new RuntimeException("AI service returned empty response");
            }
            
            // Parse structured response from AI
            String suitability = extractValue(response, "SUITABILITY:", "EXCELLENT|GOOD|MODERATE|POOR|AVOID");
            String portionStr = extractValue(response, "RECOMMENDED_PORTION:", "\\d+");
            String benefits = extractValue(response, "BENEFITS:", ".*?(?=\\n[A-Z_]+:|$)");
            String warnings = extractValue(response, "WARNINGS:", ".*?(?=\\n[A-Z_]+:|$)");
            String reasoning = extractValue(response, "REASONING:", ".*?(?=\\n[A-Z_]+:|$)");
            
            System.out.println("DEBUG: Parsed values - Suitability: " + suitability + ", Portion: " + portionStr + 
                             ", Benefits: " + benefits + ", Warnings: " + warnings + ", Reasoning: " + reasoning);
            
            // Set suitability
            setSuitability(recommendation, suitability);
            
            // Set recommended portion
            setRecommendedPortion(recommendation, portionStr);
            
            // Set benefits
            setBenefits(recommendation, benefits);
            
            // Set warnings
            setWarnings(recommendation, warnings);
            
            // Set reasoning
            setReasoning(recommendation, reasoning);
            
            // Calculate percentage of daily calories (estimate from AI response)
            calculateDailyCaloriePercentage(recommendation, user);
            
        } catch (Exception e) {
            System.err.println("DEBUG: Error parsing AI response: " + e.getMessage());
            System.err.println("DEBUG: Raw AI response was: " + (response != null ? response : "null"));
            
            // Don't use fallback - throw error with actual response for debugging
            throw new RuntimeException("Failed to parse AI response. Raw response: " + 
                (response != null ? response.substring(0, Math.min(500, response.length())) : "null") + 
                ". Error: " + e.getMessage(), e);
        }
    }
    
    private void setSuitability(FoodRecommendation recommendation, String suitability) {
        if (suitability != null) {
            recommendation.setSuitabilityScore(suitability);
            recommendation.setSuitable(!"POOR".equals(suitability) && !"AVOID".equals(suitability));
        } else {
            recommendation.setSuitabilityScore("MODERATE");
            recommendation.setSuitable(true);
        }
    }
    
    private void setRecommendedPortion(FoodRecommendation recommendation, String portionStr) {
        if (portionStr != null) {
            try {
                recommendation.setRecommendedPortionSize(Double.parseDouble(portionStr));
            } catch (NumberFormatException e) {
                recommendation.setRecommendedPortionSize(recommendation.getPortionSize());
            }
        } else {
            recommendation.setRecommendedPortionSize(recommendation.getPortionSize());
        }
    }
    
    private void setBenefits(FoodRecommendation recommendation, String benefits) {
        if (benefits != null && !benefits.trim().isEmpty()) {
            recommendation.setBenefits(Arrays.asList(benefits.split(";")));
        } else {
            recommendation.setBenefits(new ArrayList<>());
        }
    }
    
    private void setWarnings(FoodRecommendation recommendation, String warnings) {
        if (warnings != null && !warnings.trim().isEmpty() && !"None".equalsIgnoreCase(warnings.trim())) {
            recommendation.setWarnings(Arrays.asList(warnings.split(";")));
        } else {
            recommendation.setWarnings(new ArrayList<>());
        }
    }
    
    private void setReasoning(FoodRecommendation recommendation, String reasoning) {
        recommendation.setReasoning(reasoning != null ? reasoning : "AI analysis completed.");
    }
    
    private void calculateDailyCaloriePercentage(FoodRecommendation recommendation, User user) {
        double dailyCalorieNeeds = user.calculateDailyCalorieNeeds();
        // For now, use a simple estimation - this could be enhanced with AI providing calorie info
        double estimatedCalories = recommendation.getPortionSize() * 2; // Rough estimate
        double percentageOfDailyCalories = (estimatedCalories / dailyCalorieNeeds) * 100;
        recommendation.setPercentageOfDailyCalories(percentageOfDailyCalories);
    }
    
    private String extractValue(String text, String label, String pattern) {
        try {
            if (text == null || text.trim().isEmpty()) {
                System.err.println("Error extracting " + label + ": Input text is null or empty");
                return null;
            }
            
            // Look for the label followed by the pattern
            Pattern regex = Pattern.compile(label + "\\s*(.+?)(?=\\n[A-Z_]+:|$)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = regex.matcher(text);
            
            if (matcher.find()) {
                String value = matcher.group(1).trim();
                
                // For suitability, extract only the valid values
                if (label.contains("SUITABILITY")) {
                    Pattern suitabilityPattern = Pattern.compile("(EXCELLENT|GOOD|MODERATE|POOR|AVOID)", Pattern.CASE_INSENSITIVE);
                    Matcher suitabilityMatcher = suitabilityPattern.matcher(value);
                    if (suitabilityMatcher.find()) {
                        return suitabilityMatcher.group(1).toUpperCase();
                    }
                }
                
                // For portion, extract only numbers
                if (label.contains("PORTION")) {
                    Pattern numberPattern = Pattern.compile("(\\d+)");
                    Matcher numberMatcher = numberPattern.matcher(value);
                    if (numberMatcher.find()) {
                        return numberMatcher.group(1);
                    }
                }
                
                return value;
            }
        } catch (Exception e) {
            System.err.println("Error extracting " + label + ": " + e.getMessage());
        }
        
        return null;
    }
}
