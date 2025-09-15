package com.example.ssoldap.service;

import com.example.ssoldap.exception.LdapServiceException;
import com.example.ssoldap.model.AdUserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActiveDirectoryServiceTest {

    @Mock
    private LdapTemplate ldapTemplate;

    @InjectMocks
    private ActiveDirectoryService activeDirectoryService;

    @Test
    void getUserInfo_Success() {
        // Arrange
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchBase", "cn=Users,dc=test,dc=com");
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchFilter", "(sAMAccountName={0})");

        AdUserInfo expectedUser = new AdUserInfo();
        expectedUser.setUsername("testuser");
        expectedUser.setDisplayName("Test User");
        expectedUser.setEmail("testuser@test.com");
        expectedUser.setEnabled(true);

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class)))
                .thenReturn(Arrays.asList(expectedUser));

        // Act
        AdUserInfo result = activeDirectoryService.getUserInfo("testuser", "test.com");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getDisplayName());
        assertEquals("testuser@test.com", result.getEmail());
        assertTrue(result.isEnabled());
    }

    @Test
    void getUserInfo_UserNotFound() {
        // Arrange
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchBase", "cn=Users,dc=test,dc=com");
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchFilter", "(sAMAccountName={0})");

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        LdapServiceException exception = assertThrows(LdapServiceException.class, () -> {
            activeDirectoryService.getUserInfo("nonexistent", "test.com");
        });

        assertEquals("User not found: nonexistent in domain: test.com", exception.getMessage());
    }

    @Test
    void getUserInfo_MultipleUsersFound() {
        // Arrange
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchBase", "cn=Users,dc=test,dc=com");
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchFilter", "(sAMAccountName={0})");

        AdUserInfo user1 = new AdUserInfo("user1", "User One", "user1@test.com");
        AdUserInfo user2 = new AdUserInfo("user1", "User One Duplicate", "user1.dup@test.com");

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class)))
                .thenReturn(Arrays.asList(user1, user2));

        // Act
        AdUserInfo result = activeDirectoryService.getUserInfo("user1", "test.com");

        // Assert
        assertNotNull(result);
        assertEquals("User One", result.getDisplayName()); // Should return first match
    }

    @Test
    void getUserInfo_LdapException() {
        // Arrange
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchBase", "cn=Users,dc=test,dc=com");
        ReflectionTestUtils.setField(activeDirectoryService, "userSearchFilter", "(sAMAccountName={0})");

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class)))
                .thenThrow(new RuntimeException("LDAP Connection failed"));

        // Act & Assert
        LdapServiceException exception = assertThrows(LdapServiceException.class, () -> {
            activeDirectoryService.getUserInfo("testuser", "test.com");
        });

        assertTrue(exception.getMessage().contains("Failed to retrieve user information"));
    }
}