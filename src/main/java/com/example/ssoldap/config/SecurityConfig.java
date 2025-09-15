package com.example.ssoldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import waffle.servlet.spi.NegotiateSecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.spring.NegotiateSecurityFilterEntryPoint;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${ldap.domain:}")
    private String domain;

    @Value("${ldap.url:ldap://localhost:389}")
    private String ldapUrl;

    @Value("${ldap.root:dc=example,dc=com}")
    private String ldapRoot;

    @Value("${waffle.enabled:true}")
    private boolean waffleEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable());

        // Add Waffle NegotiateSecurityFilter if enabled
        if (waffleEnabled) {
            http.addFilterBefore(negotiateSecurityFilter(), BasicAuthenticationFilter.class);
            http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(negotiateSecurityFilterEntryPoint())
                .defaultAuthenticationEntryPointFor(
                    negotiateSecurityFilterEntryPoint(),
                    new AntPathRequestMatcher("/**")
                )
            );
        } else {
            // Fall back to basic authentication for LDAP-only scenarios
            http.httpBasic();
        }

        return http.build();
    }

    @Bean
    public CustomNegotiateSecurityFilter negotiateSecurityFilter() {
        CustomNegotiateSecurityFilter filter = new CustomNegotiateSecurityFilter();
        filter.configureProvider(waffleSecurityFilterProvider());
        return filter;
    }

    @Bean
    public NegotiateSecurityFilterEntryPoint negotiateSecurityFilterEntryPoint() {
        NegotiateSecurityFilterEntryPoint entryPoint = new NegotiateSecurityFilterEntryPoint();
        entryPoint.setProvider(waffleSecurityFilterProvider());
        return entryPoint;
    }

    @Bean
    public SecurityFilterProviderCollection waffleSecurityFilterProvider() {
        List<SecurityFilterProvider> providers = new ArrayList<>();
        
        NegotiateSecurityFilterProvider negotiateProvider = new NegotiateSecurityFilterProvider(
            new WindowsAuthProviderImpl()
        );
        providers.add(negotiateProvider);

        return new SecurityFilterProviderCollection(providers.toArray(new SecurityFilterProvider[0]));
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapRoot);
        if (!domain.isEmpty()) {
            contextSource.setUserDn("cn=admin," + ldapRoot);
        }
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        // Configure LDAP authentication if needed
        if (!domain.isEmpty()) {
            authBuilder.ldapAuthentication()
                    .userSearchBase("")
                    .userSearchFilter("(uid={0})")
                    .groupSearchBase("ou=groups")
                    .groupSearchFilter("(member={0})")
                    .contextSource(contextSource());
        }
        
        return authBuilder.build();
    }
}