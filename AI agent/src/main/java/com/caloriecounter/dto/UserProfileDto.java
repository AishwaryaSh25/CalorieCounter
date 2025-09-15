package com.caloriecounter.dto;

import com.caloriecounter.model.ActivityLevel;
import com.caloriecounter.model.Gender;
import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private Double weight;
    private Double height;
    private Integer age;
    private Gender gender;
    private ActivityLevel activityLevel;
    private List<String> healthConditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double bmr;
    private Double dailyCalorieNeeds;
    
    // Constructors
    public UserProfileDto() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public ActivityLevel getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevel activityLevel) { this.activityLevel = activityLevel; }
    
    public List<String> getHealthConditions() { return healthConditions; }
    public void setHealthConditions(List<String> healthConditions) { this.healthConditions = healthConditions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Double getBmr() { return bmr; }
    public void setBmr(Double bmr) { this.bmr = bmr; }
    
    public Double getDailyCalorieNeeds() { return dailyCalorieNeeds; }
    public void setDailyCalorieNeeds(Double dailyCalorieNeeds) { this.dailyCalorieNeeds = dailyCalorieNeeds; }
}
