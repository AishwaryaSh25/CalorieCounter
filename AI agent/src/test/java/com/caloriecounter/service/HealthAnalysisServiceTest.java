package com.caloriecounter.service;

import com.caloriecounter.dto.FoodRecommendation;
import com.caloriecounter.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthAnalysisServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AIService aiService; // Mocking the interface - follows DIP

    @Mock
    private AIResponseParser responseParser; // Mock the response parser

    @InjectMocks
    private HealthAnalysisService healthAnalysisService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setAge(30);
        testUser.setWeight(70.0);
        testUser.setHeight(175.0);
        testUser.setGender(Gender.MALE);
        testUser.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        testUser.setHealthConditions(Arrays.asList("None"));
    }

    @Test
    void testAnalyzeFoodForHealthyUser_ChickenBreast() {
        // Mock AI response
        String mockAIResponse = """
                SUITABILITY: EXCELLENT
                RECOMMENDED_PORTION: 150
                BENEFITS: High protein; Low fat; Muscle building
                WARNINGS: None
                REASONING: Chicken breast is an excellent choice for your health profile. It provides high-quality protein with minimal fat content.
                """;

        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), eq("chicken breast"), eq(100.0)))
                .thenReturn(mockAIResponse);

        // Mock the parser to set up the recommendation as expected
        doAnswer(invocation -> {
            FoodRecommendation rec = invocation.getArgument(1);
            rec.setSuitabilityScore("EXCELLENT");
            rec.setSuitable(true);
            rec.setRecommendedPortionSize(150.0);
            rec.setBenefits(Arrays.asList("High protein", "Low fat", "Muscle building"));
            rec.setWarnings(Arrays.asList());
            rec.setReasoning("Chicken breast is an excellent choice for your health profile. It provides high-quality protein with minimal fat content.");
            return null;
        }).when(responseParser).parseAIResponse(eq(mockAIResponse), any(FoodRecommendation.class), eq(testUser));

        // Test
        FoodRecommendation recommendation = healthAnalysisService.analyzeFoodForUser(1L, "chicken breast", 100.0);

        // Verify
        assertNotNull(recommendation);
        assertEquals("chicken breast", recommendation.getFoodName());
        assertEquals(100.0, recommendation.getPortionSize());
        assertEquals("EXCELLENT", recommendation.getSuitabilityScore());
        assertTrue(recommendation.isSuitable());
        assertEquals(150.0, recommendation.getRecommendedPortionSize());
        assertFalse(recommendation.getBenefits().isEmpty());
        assertTrue(recommendation.getWarnings().isEmpty());
        assertNotNull(recommendation.getReasoning());
    }

    @Test
    void testAnalyzeFoodForDiabeticUser_Apple() {
        // Setup diabetic user
        testUser.setHealthConditions(Arrays.asList("Diabetes"));

        String mockAIResponse = """
                SUITABILITY: GOOD
                RECOMMENDED_PORTION: 80
                BENEFITS: Fiber; Vitamin C; Natural antioxidants
                WARNINGS: Contains natural sugars - monitor blood glucose levels
                REASONING: Apple is a good choice with natural fiber and vitamins, but monitor portion size due to diabetes.
                """;

        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), eq("apple"), eq(100.0)))
                .thenReturn(mockAIResponse);

        // Mock the parser to set up the recommendation as expected
        doAnswer(invocation -> {
            FoodRecommendation rec = invocation.getArgument(1);
            rec.setSuitabilityScore("GOOD");
            rec.setSuitable(true);
            rec.setRecommendedPortionSize(80.0);
            rec.setBenefits(Arrays.asList("Fiber", "Vitamin C", "Natural antioxidants"));
            rec.setWarnings(Arrays.asList("Contains natural sugars - monitor blood glucose levels"));
            rec.setReasoning("Apple is a good choice with natural fiber and vitamins, but monitor portion size due to diabetes.");
            return null;
        }).when(responseParser).parseAIResponse(eq(mockAIResponse), any(FoodRecommendation.class), eq(testUser));

        // Test
        FoodRecommendation recommendation = healthAnalysisService.analyzeFoodForUser(1L, "apple", 100.0);

        // Verify
        assertNotNull(recommendation);
        assertEquals("apple", recommendation.getFoodName());
        assertEquals("GOOD", recommendation.getSuitabilityScore());
        assertTrue(recommendation.isSuitable());
        assertEquals(80.0, recommendation.getRecommendedPortionSize());
        assertFalse(recommendation.getBenefits().isEmpty());
        assertFalse(recommendation.getWarnings().isEmpty());
        assertTrue(recommendation.getWarnings().get(0).contains("blood glucose"));
    }

    @Test
    void testAnalyzeFoodForHypertensiveUser_Pizza() {
        // Setup hypertensive user
        testUser.setHealthConditions(Arrays.asList("Hypertension"));

        String mockAIResponse = """
                SUITABILITY: POOR
                RECOMMENDED_PORTION: 50
                BENEFITS: Provides carbohydrates and protein
                WARNINGS: Very high sodium content may increase blood pressure; Processed food with additives
                REASONING: Pizza is not recommended for your hypertension due to extremely high sodium content. Consider healthier alternatives.
                """;

        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), eq("pizza"), eq(100.0)))
                .thenReturn(mockAIResponse);

        // Mock the parser to set up the recommendation as expected
        doAnswer(invocation -> {
            FoodRecommendation rec = invocation.getArgument(1);
            rec.setSuitabilityScore("POOR");
            rec.setSuitable(false); // POOR means not suitable
            rec.setRecommendedPortionSize(50.0);
            rec.setBenefits(Arrays.asList("Provides carbohydrates and protein"));
            rec.setWarnings(Arrays.asList("Very high sodium content may increase blood pressure", "Processed food with additives"));
            rec.setReasoning("Pizza is not recommended for your hypertension due to extremely high sodium content. Consider healthier alternatives.");
            return null;
        }).when(responseParser).parseAIResponse(eq(mockAIResponse), any(FoodRecommendation.class), eq(testUser));

        // Test
        FoodRecommendation recommendation = healthAnalysisService.analyzeFoodForUser(1L, "pizza", 100.0);

        // Verify
        assertNotNull(recommendation);
        assertEquals("pizza", recommendation.getFoodName());
        assertEquals("POOR", recommendation.getSuitabilityScore());
        assertFalse(recommendation.isSuitable()); // POOR means not suitable
        assertEquals(50.0, recommendation.getRecommendedPortionSize());
        assertFalse(recommendation.getWarnings().isEmpty());
        assertTrue(recommendation.getWarnings().get(0).contains("sodium"));
    }

    @Test
    void testAnalyzeAnyFoodName() {
        // Test with any food name - this is the key benefit of our new system
        String uniqueFoodName = "homemade quinoa salad with avocado";

        String mockAIResponse = """
                SUITABILITY: EXCELLENT
                RECOMMENDED_PORTION: 120
                BENEFITS: Complete protein; Healthy fats; High fiber; Rich in vitamins
                WARNINGS: None
                REASONING: This is an excellent nutritious meal combining complete protein from quinoa with healthy fats from avocado.
                """;

        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), eq(uniqueFoodName), eq(150.0)))
                .thenReturn(mockAIResponse);

        // Mock the parser to set up the recommendation as expected
        doAnswer(invocation -> {
            FoodRecommendation rec = invocation.getArgument(1);
            rec.setSuitabilityScore("EXCELLENT");
            rec.setSuitable(true);
            rec.setRecommendedPortionSize(120.0);
            rec.setBenefits(Arrays.asList("Complete protein", "Healthy fats", "High fiber", "Rich in vitamins"));
            rec.setWarnings(Arrays.asList());
            rec.setReasoning("This is an excellent nutritious meal combining complete protein from quinoa with healthy fats from avocado.");
            return null;
        }).when(responseParser).parseAIResponse(eq(mockAIResponse), any(FoodRecommendation.class), eq(testUser));

        // Test
        FoodRecommendation recommendation = healthAnalysisService.analyzeFoodForUser(1L, uniqueFoodName, 150.0);

        // Verify
        assertNotNull(recommendation);
        assertEquals(uniqueFoodName, recommendation.getFoodName());
        assertEquals(150.0, recommendation.getPortionSize());
        assertEquals("EXCELLENT", recommendation.getSuitabilityScore());
        assertTrue(recommendation.isSuitable());
        assertEquals(120.0, recommendation.getRecommendedPortionSize());
    }

    @Test
    void testErrorHandlingForNullAIResponse() {
        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), anyString(), anyDouble()))
                .thenReturn(null);

        // Mock parser to throw exception for null response
        doThrow(new RuntimeException("AI service returned empty response"))
                .when(responseParser).parseAIResponse(isNull(), any(FoodRecommendation.class), eq(testUser));

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            healthAnalysisService.analyzeFoodForUser(1L, "test food", 100.0);
        });

        assertTrue(exception.getMessage().contains("Failed to analyze food"));
    }

    @Test
    void testErrorHandlingForEmptyAIResponse() {
        when(userService.findById(1L)).thenReturn(testUser);
        when(aiService.analyzeFood(any(User.class), anyString(), anyDouble()))
                .thenReturn("");

        // Mock parser to throw exception for empty response
        doThrow(new RuntimeException("AI service returned empty response"))
                .when(responseParser).parseAIResponse(eq(""), any(FoodRecommendation.class), eq(testUser));

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            healthAnalysisService.analyzeFoodForUser(1L, "test food", 100.0);
        });

        assertTrue(exception.getMessage().contains("Failed to analyze food"));
    }
}
