# Calorie Counter AI Agent - System Design Document

## 1. Project Overview

The Calorie Counter AI Agent is a revolutionary web application that analyzes **ANY food** using Google Gemini AI. Users create health profiles and get personalized recommendations for unlimited food options - from "homemade pasta" to "street tacos" to "grandma's apple pie".

**Key Innovation**: No food database limitations - pure AI-driven analysis for unlimited food variety.

## 2. Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Gemini API    │    │   Memory        │
│   (Thymeleaf)   │◄──►│   (Spring Boot) │◄──►│   (Gemini 2.0)  │    │   (HashMap)     │
│   - HTML/CSS/JS │    │   - REST APIs   │    │   - AI Analysis │    │   - User Data   │
│   - User Forms  │    │   - Gemini Svc  │    │   - Any Food    │    │   - Sessions    │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 3. Core Components

### 3.1 Controllers
- **WebController**: Main web interface endpoints
- **UserController**: REST API for user management
- **ErrorController**: Global error handling

### 3.2 Services
- **AIService**: Orchestrates AI analysis
- **GeminiService**: Direct Gemini API integration
- **HealthAnalysisService**: Processes AI responses into recommendations
- **UserService**: User management and profiles
- **SessionService**: In-memory session management

### 3.3 Models
- **User**: Health profile with BMR/calorie calculations
- **FoodRecommendation**: AI analysis results
- **Enums**: Gender, ActivityLevel for type safety

## 4. Key Design Decisions

### 4.1 No Food Database
**Decision**: Removed entire food database system
**Rationale**: 
- AI can analyze any food dynamically
- No limitations on food variety
- Simpler architecture
- Real-time analysis vs static data

### 4.2 Google Gemini over OpenAI
**Decision**: Switched from OpenAI to Google Gemini
**Rationale**:
- Better free tier (15 req/min vs 3)
- Higher daily limits (1,500/day)
- Excellent analysis quality
- More cost-effective for users

### 4.3 In-Memory Storage
**Decision**: Pure HashMap/ArrayList storage
**Rationale**:
- Rapid prototyping
- No database setup complexity
- Suitable for demo/testing
- Easy deployment

## 5. API Integration

### 5.1 Gemini API Flow
```
User Input → GeminiService → Gemini API → Structured Response → HealthAnalysisService → FoodRecommendation
```

### 5.2 Request Format
```json
{
  "contents": [
    {
      "parts": [
        {
          "text": "Analyze [food] for user with [health profile]..."
        }
      ]
    }
  ]
}
```

### 5.3 Expected Response Format
```
SUITABILITY: EXCELLENT|GOOD|MODERATE|POOR|AVOID
RECOMMENDED_PORTION: [number in grams]
BENEFITS: [semicolon-separated benefits]
WARNINGS: [semicolon-separated warnings or 'None']
REASONING: [detailed explanation]
```

## 6. Data Flow

### 6.1 User Registration
1. User fills health profile (pre-filled defaults)
2. UserService creates User object
3. SessionService stores user session
4. Redirect to analysis page

### 6.2 Food Analysis
1. User enters any food name
2. AIService → GeminiService → Gemini API
3. HealthAnalysisService parses AI response
4. Returns FoodRecommendation with:
   - Suitability score
   - Recommended portion
   - Benefits/warnings
   - Detailed reasoning

## 7. Security & Configuration

### 7.1 API Key Management
- Stored in `application.properties`
- Not exposed to frontend
- Single configuration point

### 7.2 Session Management
- In-memory session storage
- User ID tracking
- Automatic logout capability

## 8. Testing Strategy

### 8.1 Unit Tests
- Service layer testing with mocked AI responses
- User management functionality
- Response parsing logic

### 8.2 Integration Testing
- `/test-gemini` endpoint for API verification
- End-to-end food analysis flow
- Error handling scenarios

## 9. Deployment Considerations

### 9.1 Current State
- Development/prototype ready
- In-memory storage (data lost on restart)
- Single instance deployment

### 9.2 Production Readiness
- Would need persistent storage
- Session management improvements
- API rate limiting
- Error monitoring

## 10. Future Enhancements

### 10.1 Potential Improvements
- Database integration for persistence
- User authentication system
- Food history tracking
- Nutritional goal setting
- Mobile app development

### 10.2 Scalability Considerations
- Stateless architecture ready
- Microservices potential
- Caching strategies
- Load balancing support
