package com.example.ssoldap.config;

import com.example.ssoldap.model.CustomUserDetails;
import com.example.ssoldap.service.ActiveDirectoryUserService;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

    private final ActiveDirectoryUserService userService;

    public CustomUserDetailsContextMapper(ActiveDirectoryUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, 
                                        Collection<? extends GrantedAuthority> authorities) {
        
        // Get user details from Active Directory
        String displayName = userService.getUserDisplayName(username);
        String email = userService.getUserEmail(username);
        Set<String> userGroups = userService.getUserGroups(username);
        
        // Map AD groups to Spring Security authorities
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Check if user is in admin groups
        if (userService.isUserAdmin(userGroups)) {
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        return new CustomUserDetails(
            username,
            displayName,
            email,
            userGroups,
            mappedAuthorities
        );
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // Not needed for authentication - this method is for writing user details back to LDAP
        // which we don't need for our use case
    }
}