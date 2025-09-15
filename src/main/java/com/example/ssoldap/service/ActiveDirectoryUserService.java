package com.example.ssoldap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.*;

@Service
public class ActiveDirectoryUserService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryUserService.class);

    @Value("${ad.url}")
    private String adUrl;

    @Value("${ad.root-dn}")
    private String adRootDn;

    @Value("${ldap.username}")
    private String ldapUsername;

    @Value("${ldap.password}")
    private String ldapPassword;

    @Value("${app.admin-groups}")
    private String adminGroupsConfig;

    private Set<String> adminGroups;

    public String getUserDisplayName(String username) {
        try {
            DirContext context = getLdapContext();
            String searchFilter = String.format("(&(objectClass=user)(sAMAccountName=%s))", username);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(new String[]{"displayName", "cn"});

            NamingEnumeration<SearchResult> results = context.search(adRootDn, searchFilter, searchControls);
            
            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                Attribute displayName = attrs.get("displayName");
                if (displayName != null) {
                    return displayName.get().toString();
                }
                Attribute cn = attrs.get("cn");
                if (cn != null) {
                    return cn.get().toString();
                }
            }
            context.close();
        } catch (Exception e) {
            logger.error("Error retrieving display name for user: " + username, e);
        }
        return username; // Fallback to username if display name not found
    }

    public String getUserEmail(String username) {
        try {
            DirContext context = getLdapContext();
            String searchFilter = String.format("(&(objectClass=user)(sAMAccountName=%s))", username);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(new String[]{"mail"});

            NamingEnumeration<SearchResult> results = context.search(adRootDn, searchFilter, searchControls);
            
            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                Attribute mail = attrs.get("mail");
                if (mail != null) {
                    return mail.get().toString();
                }
            }
            context.close();
        } catch (Exception e) {
            logger.error("Error retrieving email for user: " + username, e);
        }
        return null;
    }

    public Set<String> getUserGroups(String username) {
        Set<String> groups = new HashSet<>();
        try {
            DirContext context = getLdapContext();
            String searchFilter = String.format("(&(objectClass=user)(sAMAccountName=%s))", username);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningAttributes(new String[]{"memberOf"});

            NamingEnumeration<SearchResult> results = context.search(adRootDn, searchFilter, searchControls);
            
            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                Attribute memberOf = attrs.get("memberOf");
                if (memberOf != null) {
                    NamingEnumeration<?> groupDns = memberOf.getAll();
                    while (groupDns.hasMore()) {
                        String groupDn = groupDns.next().toString();
                        String groupName = extractCnFromDn(groupDn);
                        if (groupName != null) {
                            groups.add(groupName);
                        }
                    }
                }
            }
            context.close();
        } catch (Exception e) {
            logger.error("Error retrieving groups for user: " + username, e);
        }
        return groups;
    }

    public boolean isUserAdmin(Set<String> userGroups) {
        if (adminGroups == null) {
            adminGroups = new HashSet<>();
            if (adminGroupsConfig != null && !adminGroupsConfig.isEmpty()) {
                String[] groups = adminGroupsConfig.split(",");
                for (String group : groups) {
                    adminGroups.add(group.trim());
                }
            }
        }
        
        return userGroups.stream().anyMatch(adminGroups::contains);
    }

    private DirContext getLdapContext() throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, adUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        
        return new InitialDirContext(env);
    }

    private String extractCnFromDn(String dn) {
        if (dn == null) return null;
        
        String[] parts = dn.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.toLowerCase().startsWith("cn=")) {
                return part.substring(3);
            }
        }
        return null;
    }
}