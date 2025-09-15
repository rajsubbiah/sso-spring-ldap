package com.example.ssoldap.controller;

import com.example.ssoldap.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${app.title}")
    private String appTitle;

    @Value("${app.logo-text}")
    private String logoText;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            model.addAttribute("displayName", userDetails.getDisplayName());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("email", userDetails.getEmail());
            model.addAttribute("groups", userDetails.getGroups());
            model.addAttribute("isAdmin", userDetails.hasRole("ADMIN"));
        }
        
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("logoText", logoText);
        
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("logoText", logoText);
        return "login";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            model.addAttribute("displayName", userDetails.getDisplayName());
        }
        
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("logoText", logoText);
        
        return "admin";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            model.addAttribute("displayName", userDetails.getDisplayName());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("email", userDetails.getEmail());
            model.addAttribute("groups", userDetails.getGroups());
            model.addAttribute("authorities", userDetails.getAuthorities());
        }
        
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("logoText", logoText);
        
        return "profile";
    }
}