package com.example.ssoldap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/public/health")
    public ResponseEntity<Map<String, String>> publicHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Public endpoint accessible");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> userInfo(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", principal != null ? principal.getName() : "Unknown");
        userInfo.put("authenticated", authentication != null && authentication.isAuthenticated());
        userInfo.put("authorities", authentication != null ? authentication.getAuthorities() : "None");
        
        if (authentication != null) {
            userInfo.put("authType", authentication.getClass().getSimpleName());
            userInfo.put("details", authentication.getDetails());
        }
        
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/secure")
    public ResponseEntity<Map<String, String>> secureEndpoint(Principal principal) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted to secure endpoint");
        response.put("user", principal != null ? principal.getName() : "Unknown");
        return ResponseEntity.ok(response);
    }
}