package com.example.ssoldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LDAP Configuration for Active Directory integration
 */
@Configuration
public class LdapConfig {

    @Value("${ldap.url:ldap://localhost:389}")
    private String ldapUrl;

    @Value("${ldap.base:}")
    private String ldapBase;

    @Value("${ldap.username:}")
    private String ldapUsername;

    @Value("${ldap.password:}")
    private String ldapPassword;

    @Value("${ldap.user-dn-pattern:uid={0}}")
    private String userDnPattern;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        
        if (!ldapUsername.isEmpty()) {
            contextSource.setUserDn(ldapUsername);
        }
        
        if (!ldapPassword.isEmpty()) {
            contextSource.setPassword(ldapPassword);
        }
        
        // Additional properties for Active Directory
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}