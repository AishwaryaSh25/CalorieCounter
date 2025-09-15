package com.caloriecounter.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class SessionService {
    
    // In-memory storage for Gemini API keys per session
    private final Map<String, String> geminiAPIKeys = new ConcurrentHashMap<>();
    
    public void setCurrentUser(Long userId) {
        HttpSession session = getCurrentSession();
        if (session != null) {
            session.setAttribute("currentUserId", userId);
        }
    }
    
    public Long getCurrentUserId() {
        HttpSession session = getCurrentSession();
        if (session != null) {
            return (Long) session.getAttribute("currentUserId");
        }
        return null;
    }
    
    public boolean isUserLoggedIn() {
        return getCurrentUserId() != null;
    }

    public void logout() {
        HttpSession session = getCurrentSession();
        if (session != null) {
            geminiAPIKeys.remove(session.getId());
            session.invalidate();
        }
    }
    
    private HttpSession getCurrentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
}
