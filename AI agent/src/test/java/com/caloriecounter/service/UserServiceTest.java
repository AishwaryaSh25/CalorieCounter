package com.caloriecounter.service;

import com.caloriecounter.dto.UserProfileDto;
import com.caloriecounter.dto.UserRegistrationDto;
import com.caloriecounter.model.ActivityLevel;
import com.caloriecounter.model.Gender;
import com.caloriecounter.model.User;
import com.caloriecounter.repository.UserRepository;
import com.caloriecounter.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private User user;

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

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setWeight(70.0);
        user.setHeight(175.0);
        user.setAge(30);
        user.setGender(Gender.MALE);
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        user.setHealthConditions(Arrays.asList("Diabetes"));
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserProfileDto result = userService.registerUser(registrationDto);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(70.0, result.getWeight());
        assertEquals(175.0, result.getHeight());
        assertEquals(30, result.getAge());
        assertEquals(Gender.MALE, result.getGender());
        assertEquals(ActivityLevel.MODERATELY_ACTIVE, result.getActivityLevel());
        assertTrue(result.getHealthConditions().contains("Diabetes"));
        
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(registrationDto));
        
        assertEquals("User with email john@example.com already exists", exception.getMessage());
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserProfile_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<UserProfileDto> result = userService.getUserProfile(1L);

        // Then
        assertTrue(result.isPresent());
        UserProfileDto profile = result.get();
        assertEquals("John Doe", profile.getName());
        assertEquals("john@example.com", profile.getEmail());
        assertNotNull(profile.getBmr());
        assertNotNull(profile.getDailyCalorieNeeds());
        
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserProfile_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserProfileDto> result = userService.getUserProfile(1L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void testCalculateBMR() {
        // When
        double bmr = userService.calculateBMR(user);

        // Then
        // BMR for male: 10 * weight + 6.25 * height - 5 * age + 5
        // 10 * 70 + 6.25 * 175 - 5 * 30 + 5 = 700 + 1093.75 - 150 + 5 = 1648.75
        assertEquals(1648.75, bmr, 0.01);
    }

    @Test
    void testCalculateDailyCalorieNeeds() {
        // When
        double dailyCalories = userService.calculateDailyCalorieNeeds(user);

        // Then
        double expectedBMR = 1648.75;
        double expectedDailyCalories = expectedBMR * ActivityLevel.MODERATELY_ACTIVE.getMultiplier();
        assertEquals(expectedDailyCalories, dailyCalories, 0.01);
    }

    @Test
    void testUpdateUserProfile_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRegistrationDto updateDto = new UserRegistrationDto();
        updateDto.setName("John Updated");
        updateDto.setWeight(75.0);
        updateDto.setHeight(175.0);
        updateDto.setAge(31);
        updateDto.setGender(Gender.MALE);
        updateDto.setActivityLevel(ActivityLevel.VERY_ACTIVE);
        updateDto.setHealthConditions(Arrays.asList("Diabetes", "Hypertension"));

        // When
        UserProfileDto result = userService.updateUserProfile(1L, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals(75.0, result.getWeight());
        assertEquals(31, result.getAge());
        assertEquals(ActivityLevel.VERY_ACTIVE, result.getActivityLevel());
        assertTrue(result.getHealthConditions().contains("Hypertension"));
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserProfile_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUserProfile(1L, registrationDto));
        
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.deleteUser(1L));
        
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(1L);
    }
}
