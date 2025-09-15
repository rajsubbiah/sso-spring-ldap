package com.example.ssoldap.service;

import com.example.ssoldap.exception.LdapServiceException;
import com.example.ssoldap.model.AdUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for retrieving user information from Active Directory
 */
@Service
public class ActiveDirectoryService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryService.class);

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${ldap.user.search-base:}")
    private String userSearchBase;

    @Value("${ldap.user.search-filter:(sAMAccountName={0})}")
    private String userSearchFilter;

    /**
     * Retrieves user information from Active Directory for a given username and domain
     *
     * @param username the username to search for
     * @param domain   the domain (can be used to construct search base if needed)
     * @return AdUserInfo containing user details
     * @throws LdapServiceException if user not found or LDAP error occurs
     */
    public AdUserInfo getUserInfo(String username, String domain) {
        logger.info("Searching for user: {} in domain: {}", username, domain);
        
        try {
            // Construct search base if domain is provided and userSearchBase is empty
            String searchBase = userSearchBase;
            if (searchBase.isEmpty() && domain != null && !domain.isEmpty()) {
                searchBase = constructSearchBaseFromDomain(domain);
            }

            List<AdUserInfo> users = ldapTemplate.search(
                LdapQueryBuilder.query()
                    .base(searchBase)
                    .filter(userSearchFilter, username),
                new UserAttributesMapper()
            );

            if (users.isEmpty()) {
                throw new LdapServiceException("User not found: " + username + " in domain: " + domain);
            }

            if (users.size() > 1) {
                logger.warn("Multiple users found for username: {}. Returning first match.", username);
            }

            AdUserInfo userInfo = users.get(0);
            logger.info("Successfully retrieved user info for: {}", username);
            return userInfo;

        } catch (LdapServiceException e) {
            // Re-throw LdapServiceException as-is
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving user info for username: {} in domain: {}", username, domain, e);
            throw new LdapServiceException("Failed to retrieve user information", e);
        }
    }

    /**
     * Constructs LDAP search base from domain name
     * Example: "example.com" -> "DC=example,DC=com"
     */
    private String constructSearchBaseFromDomain(String domain) {
        return Arrays.stream(domain.split("\\."))
                .map(part -> "DC=" + part)
                .collect(Collectors.joining(","));
    }

    /**
     * AttributesMapper implementation to map LDAP attributes to AdUserInfo object
     */
    private static class UserAttributesMapper implements AttributesMapper<AdUserInfo> {
        @Override
        public AdUserInfo mapFromAttributes(Attributes attributes) throws NamingException {
            AdUserInfo user = new AdUserInfo();

            // Map common Active Directory attributes
            user.setUsername(getAttributeValue(attributes, "sAMAccountName"));
            user.setDisplayName(getAttributeValue(attributes, "displayName"));
            user.setEmail(getAttributeValue(attributes, "mail"));
            user.setFirstName(getAttributeValue(attributes, "givenName"));
            user.setLastName(getAttributeValue(attributes, "sn"));
            user.setDepartment(getAttributeValue(attributes, "department"));
            user.setTitle(getAttributeValue(attributes, "title"));
            user.setPhone(getAttributeValue(attributes, "telephoneNumber"));
            user.setDistinguishedName(getAttributeValue(attributes, "distinguishedName"));

            // Handle userAccountControl to determine if user is enabled
            String userAccountControl = getAttributeValue(attributes, "userAccountControl");
            if (userAccountControl != null) {
                try {
                    int control = Integer.parseInt(userAccountControl);
                    // Bit 2 (0x2) indicates disabled account
                    user.setEnabled((control & 2) == 0);
                } catch (NumberFormatException e) {
                    user.setEnabled(true); // Default to enabled if can't parse
                }
            } else {
                user.setEnabled(true);
            }

            // Handle memberOf attribute (groups)
            if (attributes.get("memberOf") != null) {
                List<String> groups = new java.util.ArrayList<>();
                for (int i = 0; i < attributes.get("memberOf").size(); i++) {
                    groups.add((String) attributes.get("memberOf").get(i));
                }
                user.setMemberOf(groups);
            }

            return user;
        }

        private String getAttributeValue(Attributes attributes, String attributeName) {
            try {
                return attributes.get(attributeName) != null ? 
                    (String) attributes.get(attributeName).get() : null;
            } catch (NamingException e) {
                return null;
            }
        }
    }
}