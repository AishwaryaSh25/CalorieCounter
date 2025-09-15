package com.caloriecounter.controller;

import com.caloriecounter.dto.FoodAnalysisRequest;
import com.caloriecounter.dto.FoodRecommendation;
import com.caloriecounter.dto.UserProfileDto;
import com.caloriecounter.dto.UserRegistrationDto;
import com.caloriecounter.model.ActivityLevel;

import com.caloriecounter.model.Gender;
import com.caloriecounter.model.User;


import com.caloriecounter.repository.UserRepository;

import com.caloriecounter.service.HealthAnalysisService;
import com.caloriecounter.service.SessionService;
import com.caloriecounter.service.UserService;
import com.caloriecounter.service.AIService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {
    
    @Autowired
    private UserService userService;
    

    
    @Autowired
    private HealthAnalysisService healthAnalysisService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AIService aiService; // Now properly injected via interface


    
    @GetMapping("/")
    public String home(Model model) {
        System.out.println("DEBUG: Home page accessed");
        model.addAttribute("title", "Calorie Counter AI Agent");
        return "index";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Create a UserRegistrationDto with default values
        UserRegistrationDto defaultUser = new UserRegistrationDto();
        defaultUser.setName("John Doe");
        defaultUser.setEmail("john.doe@example.com");
        defaultUser.setAge(30);
        defaultUser.setGender(Gender.MALE);
        defaultUser.setWeight(70.0);
        defaultUser.setHeight(175.0);
        defaultUser.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        model.addAttribute("user", defaultUser);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("activityLevels", ActivityLevel.values());
        model.addAttribute("commonHealthConditions", Arrays.asList(
            "Diabetes", "Hypertension", "Heart Disease", "Obesity",
            "Kidney Disease", "High Cholesterol", "Food Allergies"
        ));
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("activityLevels", ActivityLevel.values());
            model.addAttribute("commonHealthConditions", Arrays.asList(
                "Diabetes", "Hypertension", "Heart Disease", "Obesity", 
                "Kidney Disease", "High Cholesterol", "Food Allergies"
            ));
            return "register";
        }
        
        try {
            UserProfileDto profile = userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Welcome, " + profile.getName());
            return "redirect:/profile/" + profile.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("activityLevels", ActivityLevel.values());
            model.addAttribute("commonHealthConditions", Arrays.asList(
                "Diabetes", "Hypertension", "Heart Disease", "Obesity", 
                "Kidney Disease", "High Cholesterol", "Food Allergies"
            ));
            return "register";
        }
    }
    
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        Optional<UserProfileDto> profile = userService.getUserProfile(id);
        if (profile.isPresent()) {
            model.addAttribute("user", profile.get());
            return "profile";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }
    }
    
    @GetMapping("/analyze")
    public String showAnalyzeForm(Model model) {
        if (!sessionService.isUserLoggedIn()) {
            System.out.println("DEBUG: User not logged in, redirecting to register");
            return "redirect:/register";
        }
        Long currentUserId = sessionService.getCurrentUserId();
        System.out.println("DEBUG: showAnalyzeForm - Current User ID: " + currentUserId);

        // Create FoodAnalysisRequest with default values
        FoodAnalysisRequest defaultRequest = new FoodAnalysisRequest();
        defaultRequest.setFoodName("chicken breast");
        defaultRequest.setPortionSizeInGrams(100.0);

        model.addAttribute("analysisRequest", defaultRequest);
        model.addAttribute("currentUserId", currentUserId);
        return "analyze";
    }
    
    @PostMapping("/analyze")
    public String analyzeFood(@Valid @ModelAttribute("analysisRequest") FoodAnalysisRequest request,
                             BindingResult result, Model model) {
        System.out.println("DEBUG: analyzeFood method called");
        System.out.println("DEBUG: Request - Food: " + request.getFoodName() + ", Portion: " + request.getPortionSizeInGrams() + ", UserID: " + request.getUserId());

        if (!sessionService.isUserLoggedIn()) {
            System.out.println("DEBUG: User not logged in, redirecting to register");
            return "redirect:/register";
        }

        if (result.hasErrors()) {
            System.out.println("DEBUG: Form validation errors: " + result.getAllErrors());
            model.addAttribute("currentUserId", sessionService.getCurrentUserId());
            model.addAttribute("errorMessage", "Please fill in all required fields correctly.");
            return "analyze";
        }

        try {
            // Get current user from session
            Long currentUserId = sessionService.getCurrentUserId();
            System.out.println("DEBUG: Current User ID from session: " + currentUserId);

            if (currentUserId == null) {
                model.addAttribute("errorMessage", "No user session found. Please register first.");
                return "redirect:/register";
            }

            Optional<User> userOpt = userRepository.findById(currentUserId);
            if (!userOpt.isPresent()) {
                model.addAttribute("errorMessage", "User session expired. Please log in again.");
                return "redirect:/register";
            }

            User user = userOpt.get();
            System.out.println("DEBUG: Analyzing " + request.getFoodName() + " for user " + user.getName());

            // Analyze food directly with AI - no food database needed
            FoodRecommendation recommendation = healthAnalysisService.analyzeFoodForUser(
                    request.getUserId(),
                    request.getFoodName(),
                    request.getPortionSizeInGrams());

            System.out.println("DEBUG: Analysis complete, suitability: " + recommendation.getSuitabilityScore());

            model.addAttribute("recommendation", recommendation);
            model.addAttribute("user", user);

            return "result";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error analyzing food: " + e.getMessage());
            model.addAttribute("currentUserId", sessionService.getCurrentUserId());
            e.printStackTrace(); // Debug log
            return "analyze";
        }
    }
    

    
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/logout")
    public String logout() {
        sessionService.logout();
        return "redirect:/";
    }

    @GetMapping("/test-gemini")
    @ResponseBody
    public String testGemini(@RequestParam(required = false) String apiKey) {
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return "ERROR: Please provide your Gemini API key as a parameter: /test-gemini?apiKey=YOUR_API_KEY\n\n" +
                       "Get your free API key from: https://makersuite.google.com/app/apikey";
            }

            // Create a simple test user with the provided API key
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setAge(30);
            testUser.setWeight(70.0);
            testUser.setHeight(175.0);
            testUser.setGender(Gender.MALE);
            testUser.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
            testUser.setGeminiApiKey(apiKey);

            String result = aiService.analyzeFood(testUser, "Apple", 100.0);
            return "SUCCESS: Gemini responded with " + result.length() + " characters.\n\nFirst 500 chars:\n" +
                   result.substring(0, Math.min(500, result.length())) + "\n\n[Response truncated...]";

        } catch (Exception e) {
            return "ERROR: " + e.getMessage() + "\n\nStack trace:\n" +
                   java.util.Arrays.toString(e.getStackTrace()).replace(",", "\n");
        }
    }


}
