package com.example.ssoldap.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import waffle.servlet.NegotiateSecurityFilter;
import waffle.servlet.spi.SecurityFilterProviderCollection;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomNegotiateSecurityFilter extends NegotiateSecurityFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Skip authentication for public endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/public/") || requestURI.startsWith("/actuator/")) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is already authenticated via Spring Security
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.isAuthenticated() && 
            !existingAuth.getName().equals("anonymousUser")) {
            // User already authenticated, proceed with request
            chain.doFilter(request, response);
            return;
        }

        try {
            // Try Waffle authentication
            super.doFilter(req, res, chain);
        } catch (Exception e) {
            // If Waffle authentication fails, don't return 403
            // Instead, let Spring Security handle it (may fall back to LDAP)
            System.err.println("Waffle authentication failed: " + e.getMessage());
            
            // Clear any partial authentication state
            SecurityContextHolder.clearContext();
            
            // Continue with the filter chain to allow Spring Security to handle authentication
            chain.doFilter(request, response);
        }
    }

    public void configureProvider(SecurityFilterProviderCollection provider) {
        // Configure the provider if needed
        // This replaces the setProvider method which may not be available in this version
    }
}