package com.example.sso.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/user/current")
    public Map<String, Object> getCurrentUser(Principal principal, Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        
        // Get current user from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            userInfo.put("username", auth.getName());
            userInfo.put("authenticated", auth.isAuthenticated());
            userInfo.put("authorities", auth.getAuthorities());
            userInfo.put("principal", auth.getPrincipal().toString());
        }
        
        // Also provide Principal information if available
        if (principal != null) {
            userInfo.put("principalName", principal.getName());
        }
        
        return userInfo;
    }

    @GetMapping("/api/user/info")
    public Map<String, Object> getUserInfo() {
        Map<String, Object> info = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            info.put("name", auth.getName());
            info.put("authenticated", auth.isAuthenticated());
            
            // Extract Windows domain and username if available
            String fullName = auth.getName();
            if (fullName.contains("\\")) {
                String[] parts = fullName.split("\\\\");
                info.put("domain", parts[0]);
                info.put("username", parts[1]);
            } else {
                info.put("username", fullName);
            }
        } else {
            info.put("message", "No authenticated user found");
        }
        
        return info;
    }

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "Unknown";
        return "Hello " + username + "! You are successfully authenticated using Waffle.";
    }
}