package com.example.ssoldap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "waffle.enabled=false", // Disable Waffle for tests to avoid Windows-specific requirements
    "ldap.domain=",
    "ldap.url=ldap://localhost:389"
})
class SsoLdapApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context can load successfully
    }
}