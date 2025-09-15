package com.example.ssoldap.controller;

import com.example.ssoldap.exception.LdapServiceException;
import com.example.ssoldap.model.AdUserInfo;
import com.example.ssoldap.service.ActiveDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for Active Directory user operations
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    /**
     * Get user information from Active Directory
     *
     * @param username the username to search for
     * @param domain   the domain name (optional)
     * @return user information
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(
            @RequestParam @NotBlank(message = "Username is required") String username,
            @RequestParam(required = false) String domain) {

        logger.info("Received request to get user info for username: {} in domain: {}", username, domain);

        try {
            AdUserInfo userInfo = activeDirectoryService.getUserInfo(username, domain);
            return ResponseEntity.ok(userInfo);
        } catch (LdapServiceException e) {
            logger.error("Error getting user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error getting user info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "An unexpected error occurred"));
        }
    }

    /**
     * Get user information using path variables
     *
     * @param username the username to search for
     * @param domain   the domain name
     * @return user information
     */
    @GetMapping("/info/{domain}/{username}")
    public ResponseEntity<?> getUserInfoByPath(
            @PathVariable @NotBlank(message = "Username is required") String username,
            @PathVariable @NotBlank(message = "Domain is required") String domain) {

        logger.info("Received request to get user info for username: {} in domain: {}", username, domain);

        try {
            AdUserInfo userInfo = activeDirectoryService.getUserInfo(username, domain);
            return ResponseEntity.ok(userInfo);
        } catch (LdapServiceException e) {
            logger.error("Error getting user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error getting user info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "An unexpected error occurred"));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Active Directory User Service");
        return ResponseEntity.ok(response);
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationException(jakarta.validation.ConstraintViolationException e) {
        logger.error("Validation error: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(createErrorResponse("Validation error", e.getMessage()));
    }

    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }
}