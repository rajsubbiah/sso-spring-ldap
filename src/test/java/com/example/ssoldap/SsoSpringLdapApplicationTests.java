package com.example.ssoldap;

import com.example.ssoldap.controller.UserController;
import com.example.ssoldap.service.ActiveDirectoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
    "ldap.url=ldap://localhost:389",
    "ldap.base=dc=test,dc=com"
})
class SsoSpringLdapApplicationTests {

    @Autowired
    private UserController userController;
    
    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @Test
    void contextLoads() {
        assertNotNull(userController);
        assertNotNull(activeDirectoryService);
    }

    @Test
    void applicationStarts() {
        // This test verifies that the Spring context loads without errors
    }
}