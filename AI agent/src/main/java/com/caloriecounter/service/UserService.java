package com.caloriecounter.service;

import com.caloriecounter.dto.UserProfileDto;
import com.caloriecounter.dto.UserRegistrationDto;
import com.caloriecounter.model.User;
import com.caloriecounter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;
    
    public UserProfileDto registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("User with email " + registrationDto.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setWeight(registrationDto.getWeight());
        user.setHeight(registrationDto.getHeight());
        user.setAge(registrationDto.getAge());
        user.setGender(registrationDto.getGender());
        user.setActivityLevel(registrationDto.getActivityLevel());
        user.setHealthConditions(registrationDto.getHealthConditions());
        user.setGeminiApiKey(registrationDto.getGeminiApiKey());

        // Save user
        User savedUser = userRepository.save(user);

        // Set user session
        sessionService.setCurrentUser(savedUser.getId());

        return convertToProfileDto(savedUser);
    }
    
    public Optional<UserProfileDto> getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToProfileDto);
    }
    
    public Optional<UserProfileDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToProfileDto);
    }
    
    public UserProfileDto updateUserProfile(Long userId, UserRegistrationDto updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Update user fields
        user.setName(updateDto.getName());
        user.setWeight(updateDto.getWeight());
        user.setHeight(updateDto.getHeight());
        user.setAge(updateDto.getAge());
        user.setGender(updateDto.getGender());
        user.setActivityLevel(updateDto.getActivityLevel());
        user.setHealthConditions(updateDto.getHealthConditions());
        
        User updatedUser = userRepository.save(user);
        return convertToProfileDto(updatedUser);
    }
    
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
    
    public double calculateBMR(User user) {
        return user.calculateBMR();
    }
    
    public double calculateDailyCalorieNeeds(User user) {
        return user.calculateDailyCalorieNeeds();
    }
    
    private UserProfileDto convertToProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setActivityLevel(user.getActivityLevel());
        dto.setHealthConditions(user.getHealthConditions());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setBmr(user.calculateBMR());
        dto.setDailyCalorieNeeds(user.calculateDailyCalorieNeeds());
        return dto;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
