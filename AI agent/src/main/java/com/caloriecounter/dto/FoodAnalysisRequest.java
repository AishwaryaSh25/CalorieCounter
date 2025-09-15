package com.caloriecounter.dto;

import jakarta.validation.constraints.*;

public class FoodAnalysisRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Food name is required")
    private String foodName;
    
    @DecimalMin(value = "1.0", message = "Portion size must be at least 1 gram")
    @DecimalMax(value = "2000.0", message = "Portion size must be less than 2000 grams")
    private Double portionSizeInGrams = 100.0; // Default to 100g
    
    // Constructors
    public FoodAnalysisRequest() {}
    
    public FoodAnalysisRequest(Long userId, String foodName, Double portionSizeInGrams) {
        this.userId = userId;
        this.foodName = foodName;
        this.portionSizeInGrams = portionSizeInGrams;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    
    public Double getPortionSizeInGrams() { return portionSizeInGrams; }
    public void setPortionSizeInGrams(Double portionSizeInGrams) { this.portionSizeInGrams = portionSizeInGrams; }
}
