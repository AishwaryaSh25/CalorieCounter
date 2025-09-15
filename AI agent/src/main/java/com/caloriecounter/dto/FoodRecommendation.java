package com.caloriecounter.dto;

import java.util.List;

public class FoodRecommendation {
    private String foodName;
    private Double portionSize;
    private boolean suitable;
    private double recommendedPortionSize;
    private String suitabilityScore; // EXCELLENT, GOOD, MODERATE, POOR, AVOID
    private String reasoning;
    private List<String> warnings;
    private List<String> benefits;
    private double percentageOfDailyCalories;
    
    // Constructors
    public FoodRecommendation() {}
    
    // Getters and Setters
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Double getPortionSize() { return portionSize; }
    public void setPortionSize(Double portionSize) { this.portionSize = portionSize; }
    
    public boolean isSuitable() { return suitable; }
    public void setSuitable(boolean suitable) { this.suitable = suitable; }
    
    public double getRecommendedPortionSize() { return recommendedPortionSize; }
    public void setRecommendedPortionSize(double recommendedPortionSize) { this.recommendedPortionSize = recommendedPortionSize; }
    
    public String getSuitabilityScore() { return suitabilityScore; }
    public void setSuitabilityScore(String suitabilityScore) { this.suitabilityScore = suitabilityScore; }
    
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }
    
    public double getPercentageOfDailyCalories() { return percentageOfDailyCalories; }
    public void setPercentageOfDailyCalories(double percentageOfDailyCalories) { this.percentageOfDailyCalories = percentageOfDailyCalories; }
}
