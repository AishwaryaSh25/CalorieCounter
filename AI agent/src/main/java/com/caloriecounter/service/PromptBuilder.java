package com.caloriecounter.service;

import com.caloriecounter.model.User;
import org.springframework.stereotype.Component;

/**
 * Utility class for building AI prompts.
 * Follows Single Responsibility Principle (SRP) - only responsible for prompt construction.
 */
@Component
public class PromptBuilder {

    /**
     * Builds a nutrition analysis prompt for AI services.
     *
     * @param user The user with health profile information
     * @param foodName The name of the food to analyze
     * @param portionSize The portion size in grams
     * @return Formatted prompt string for AI analysis
     */
    public String buildNutritionPrompt(User user, String foodName, double portionSize) {
        StringBuilder prompt = new StringBuilder();

        // System instruction
        prompt.append("You are a professional nutritionist AI. Analyze this food for the user and provide personalized recommendations.\n\n");

        // User profile section
        appendUserProfile(prompt, user);

        // Food information section
        appendFoodInformation(prompt, foodName, portionSize);

        // Response format instructions
        appendResponseFormat(prompt);

        return prompt.toString();
    }

    private void appendUserProfile(StringBuilder prompt, User user) {
        prompt.append("USER PROFILE:\n");
        prompt.append("- Name: ").append(user.getName()).append("\n");
        prompt.append("- Age: ").append(user.getAge()).append(" years\n");
        prompt.append("- Gender: ").append(user.getGender()).append("\n");
        prompt.append("- Weight: ").append(user.getWeight()).append(" kg\n");
        prompt.append("- Height: ").append(user.getHeight()).append(" cm\n");
        prompt.append("- Activity Level: ").append(user.getActivityLevel().getDescription()).append("\n");
        prompt.append("- Daily Calorie Needs: ").append(String.format("%.0f", user.calculateDailyCalorieNeeds())).append(" calories\n");

        if (user.getHealthConditions() != null && !user.getHealthConditions().isEmpty()) {
            prompt.append("- Health Conditions: ").append(String.join(", ", user.getHealthConditions())).append("\n");
        }
    }

    private void appendFoodInformation(StringBuilder prompt, String foodName, double portionSize) {
        prompt.append("\nFOOD TO ANALYZE:\n");
        prompt.append("- Food: ").append(foodName).append("\n");
        prompt.append("- Portion Size: ").append(portionSize).append(" grams\n");
        prompt.append("- Please analyze the nutritional content of this food and provide recommendations\n");
    }

    private void appendResponseFormat(StringBuilder prompt) {
        prompt.append("\nPLEASE PROVIDE YOUR ANALYSIS IN THIS EXACT FORMAT:\n");
        prompt.append("SUITABILITY: [EXCELLENT/GOOD/MODERATE/POOR/AVOID]\n");
        prompt.append("RECOMMENDED_PORTION: [number in grams]\n");
        prompt.append("BENEFITS: [list benefits separated by semicolons]\n");
        prompt.append("WARNINGS: [list warnings separated by semicolons, or 'None' if no warnings]\n");
        prompt.append("REASONING: [detailed explanation of your recommendation considering the user's profile]\n");
    }
}
