package com.example.ssoldap.config;

import com.example.ssoldap.service.ActiveDirectoryUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

// Windows authentication imports commented out - waffle library not available in central repo
// import waffle.spring.NegotiateSecurityFilter;
// import waffle.spring.NegotiateSecurityFilterEntryPoint;
// import waffle.windows.auth.impl.WindowsAuthProviderImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${ad.domain}")
    private String adDomain;

    @Value("${ad.url}")
    private String adUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider(ActiveDirectoryUserService userService) {
        ActiveDirectoryLdapAuthenticationProvider provider = 
            new ActiveDirectoryLdapAuthenticationProvider(adDomain, adUrl);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setUserDetailsContextMapper(new CustomUserDetailsContextMapper(userService));
        return provider;
    }

    // Windows authentication beans commented out - waffle library not available in central repo
    /*
    @Bean
    public NegotiateSecurityFilterEntryPoint negotiateSecurityFilterEntryPoint() {
        NegotiateSecurityFilterEntryPoint entryPoint = new NegotiateSecurityFilterEntryPoint();
        entryPoint.setProvider(new WindowsAuthProviderImpl());
        return entryPoint;
    }

    @Bean
    public NegotiateSecurityFilter negotiateSecurityFilter() {
        NegotiateSecurityFilter filter = new NegotiateSecurityFilter();
        filter.setProvider(new WindowsAuthProviderImpl());
        filter.setPrincipalFormat("fqn");
        filter.setRoleFormat("both");
        return filter;
    }
    */
}