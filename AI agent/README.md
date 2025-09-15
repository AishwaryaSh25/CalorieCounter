# Calorie Counter AI Agent

AI-powered food analysis for **ANY food** using Google Gemini. Enter any food name and get personalized health recommendations.

## 🚀 Quick Start

1. **Get Gemini API Key**: https://makersuite.google.com/app/apikey
2. **Run**: IntelliJ IDEA → Run `CalorieCounterApplication`
3. **Register**: http://localhost:8080 → Enter your API key during signup
4. **Analyze**: Enter any food name and get AI recommendations

## ✨ Key Features

- **🍕 Any Food**: Pizza, sushi, homemade curry - analyze anything
- **🤖 Gemini AI**: Advanced AI analysis with better free limits
- **👤 Health Profiles**: Personalized recommendations based on your health
- **⚡ No Database**: Pure in-memory storage, no setup required
- **📱 Web Interface**: Clean, responsive design

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot
- **AI**: Google Gemini 2.0 Flash
- **Frontend**: Thymeleaf, Bootstrap
- **Storage**: In-memory (HashMap)

## 📖 Documentation

- [System Design](SYSTEM_DESIGN.md) - Architecture and technical details
- [Interaction History](INTERACTIONS.md) - Development chat summary

## 🧪 Testing

- **Test Gemini**: http://localhost:8080/test-gemini?apiKey=YOUR_API_KEY
- **Run Tests**: IntelliJ → Right-click test files → Run
- **Example Foods**: "chicken breast", "homemade pasta", "street tacos"

## 🔧 Configuration

```properties
# application.properties
# API keys are now provided by users during registration
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
```

## 📝 Usage Flow

1. **Register** with health profile and your Gemini API key
2. **Analyze** any food name (unlimited variety)
3. **Get** personalized AI recommendations with:
   - Suitability score (EXCELLENT/GOOD/MODERATE/POOR)
   - Recommended portion size
   - Health benefits and warnings
   - Detailed reasoning

---

**Note**: This is a prototype using in-memory storage. Data is lost on restart.
