package com.example.ssoldap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class SsoLdapApplicationTests {

    @Test
    void contextLoads() {
        // Test that Spring Boot application context loads successfully
        // This test validates the basic configuration without requiring
        // an actual LDAP server connection
    }
}