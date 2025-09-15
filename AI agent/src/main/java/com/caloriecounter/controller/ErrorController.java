package com.caloriecounter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        
        System.out.println("DEBUG: Error occurred - Status: " + statusCode + ", URI: " + requestUri + ", Message: " + errorMessage);
        
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestUri", requestUri);
        
        if (statusCode != null && statusCode == 404) {
            model.addAttribute("title", "Page Not Found");
            model.addAttribute("suggestion", "Try going to the home page or check the URL");
        } else {
            model.addAttribute("title", "Error");
        }
        
        return "error";
    }
}
