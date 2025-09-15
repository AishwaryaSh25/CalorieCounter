package com.caloriecounter.controller;

import com.caloriecounter.dto.UserProfileDto;
import com.caloriecounter.dto.UserRegistrationDto;
import com.caloriecounter.model.ActivityLevel;
import com.caloriecounter.model.Gender;
import com.caloriecounter.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;
    private UserProfileDto profileDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setName("John Doe");
        registrationDto.setEmail("john@example.com");
        registrationDto.setWeight(70.0);
        registrationDto.setHeight(175.0);
        registrationDto.setAge(30);
        registrationDto.setGender(Gender.MALE);
        registrationDto.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        registrationDto.setHealthConditions(Arrays.asList("Diabetes"));
        registrationDto.setGeminiApiKey("AIza-test-api-key-for-testing");

        profileDto = new UserProfileDto();
        profileDto.setId(1L);
        profileDto.setName("John Doe");
        profileDto.setEmail("john@example.com");
        profileDto.setWeight(70.0);
        profileDto.setHeight(175.0);
        profileDto.setAge(30);
        profileDto.setGender(Gender.MALE);
        profileDto.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        profileDto.setHealthConditions(Arrays.asList("Diabetes"));
        profileDto.setCreatedAt(LocalDateTime.now());
        profileDto.setUpdatedAt(LocalDateTime.now());
        profileDto.setBmr(1648.75);
        profileDto.setDailyCalorieNeeds(2555.56);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Given
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(profileDto);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.weight").value(70.0))
                .andExpect(jsonPath("$.height").value(175.0))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.activityLevel").value("MODERATELY_ACTIVE"))
                .andExpect(jsonPath("$.bmr").value(1648.75))
                .andExpect(jsonPath("$.dailyCalorieNeeds").value(2555.56));
    }

    @Test
    void testRegisterUser_ValidationError() throws Exception {
        // Given - invalid registration data
        UserRegistrationDto invalidDto = new UserRegistrationDto();
        invalidDto.setName(""); // Empty name
        invalidDto.setEmail("invalid-email"); // Invalid email
        invalidDto.setWeight(-10.0); // Invalid weight
        invalidDto.setAge(5); // Invalid age

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        // Given
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("User with email john@example.com already exists"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with email john@example.com already exists"));
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        // Given
        when(userService.getUserProfile(1L)).thenReturn(Optional.of(profileDto));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.bmr").value(1648.75))
                .andExpect(jsonPath("$.dailyCalorieNeeds").value(2555.56));
    }

    @Test
    void testGetUserProfile_NotFound() throws Exception {
        // Given
        when(userService.getUserProfile(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 1"));
    }

    @Test
    void testGetUserByEmail_Success() throws Exception {
        // Given
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(profileDto));

        // When & Then
        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        // Given
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with email: john@example.com"));
    }

    @Test
    void testUpdateUserProfile_Success() throws Exception {
        // Given
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setId(1L);
        updatedProfile.setName("John Updated");
        updatedProfile.setWeight(75.0);
        updatedProfile.setAge(31);
        
        when(userService.updateUserProfile(anyLong(), any(UserRegistrationDto.class)))
                .thenReturn(updatedProfile);

        UserRegistrationDto updateDto = new UserRegistrationDto();
        updateDto.setName("John Updated");
        updateDto.setEmail("john@example.com");
        updateDto.setWeight(75.0);
        updateDto.setHeight(175.0);
        updateDto.setAge(31);
        updateDto.setGender(Gender.MALE);
        updateDto.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        updateDto.setGeminiApiKey("AIza-test-api-key-for-testing");

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    void testUpdateUserProfile_NotFound() throws Exception {
        // Given
        when(userService.updateUserProfile(anyLong(), any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("User not found with id: 1"));

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 1"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found with id: 1"))
                .when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 1"));
    }
}
