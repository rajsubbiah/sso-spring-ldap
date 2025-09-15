package com.example.ssoldap.controller;

import com.example.ssoldap.config.TestSecurityConfig;
import com.example.ssoldap.exception.LdapServiceException;
import com.example.ssoldap.model.AdUserInfo;
import com.example.ssoldap.service.ActiveDirectoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "ldap.url=ldap://localhost:389",
    "ldap.base=dc=test,dc=com"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActiveDirectoryService activeDirectoryService;

    @Test
    void getUserInfo_Success() throws Exception {
        // Arrange
        AdUserInfo userInfo = new AdUserInfo();
        userInfo.setUsername("testuser");
        userInfo.setDisplayName("Test User");
        userInfo.setEmail("testuser@test.com");
        userInfo.setFirstName("Test");
        userInfo.setLastName("User");
        userInfo.setDepartment("IT");
        userInfo.setTitle("Developer");
        userInfo.setEnabled(true);
        userInfo.setMemberOf(Arrays.asList("Domain Users", "Developers"));

        when(activeDirectoryService.getUserInfo("testuser", "test.com"))
                .thenReturn(userInfo);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/info")
                        .param("username", "testuser")
                        .param("domain", "test.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.email").value("testuser@test.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.title").value("Developer"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.memberOf").isArray())
                .andExpect(jsonPath("$.memberOf[0]").value("Domain Users"))
                .andExpect(jsonPath("$.memberOf[1]").value("Developers"));
    }

    @Test
    void getUserInfoByPath_Success() throws Exception {
        // Arrange
        AdUserInfo userInfo = new AdUserInfo("testuser", "Test User", "testuser@test.com");
        when(activeDirectoryService.getUserInfo("testuser", "test.com"))
                .thenReturn(userInfo);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/info/test.com/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.email").value("testuser@test.com"));
    }

    @Test
    void getUserInfo_UserNotFound() throws Exception {
        // Arrange
        when(activeDirectoryService.getUserInfo("nonexistent", "test.com"))
                .thenThrow(new LdapServiceException("User not found: nonexistent in domain: test.com"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/info")
                        .param("username", "nonexistent")
                        .param("domain", "test.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found: nonexistent in domain: test.com"));
    }

    @Test
    void getUserInfo_MissingUsername() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/info")
                        .param("domain", "test.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserInfo_InternalServerError() throws Exception {
        // Arrange
        when(activeDirectoryService.getUserInfo("testuser", "test.com"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/info")
                        .param("username", "testuser")
                        .param("domain", "test.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void health_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Active Directory User Service"));
    }
}