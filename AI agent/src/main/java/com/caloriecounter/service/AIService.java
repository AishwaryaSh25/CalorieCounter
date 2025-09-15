package com.caloriecounter.service;

import com.caloriecounter.model.User;

/**
 * Interface for AI-powered food analysis services.
 * Follows the Interface Segregation Principle (ISP) and Dependency Inversion Principle (DIP).
 */
public interface AIService {

    /**
     * Analyzes a food item for a specific user and portion size.
     *
     * @param user The user with health profile information
     * @param foodName The name of the food to analyze
     * @param portionSize The portion size in grams
     * @return AI analysis response as structured text
     */
    String analyzeFood(User user, String foodName, double portionSize);
}
