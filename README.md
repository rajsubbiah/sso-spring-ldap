# SSO Spring LDAP

A Spring Boot application that provides Single Sign-On (SSO) integration with LDAP and Windows Authentication via the Waffle library.

## Features

- **LDAP Authentication**: Support for LDAP and Active Directory authentication
- **Windows Authentication**: Integration with Waffle library for Windows Negotiate/NTLM authentication
- **Custom Security Filter**: Custom `NegotiateSecurityFilter` that resolves 403 errors when integrating both authentication methods
- **Flexible Configuration**: Enable/disable authentication methods via configuration properties

## Key Components

### CustomNegotiateSecurityFilter

The core solution to the 403 error issue when adding Waffle library. This custom filter:

1. **Graceful Fallback**: If Waffle authentication fails, it doesn't return 403 but allows Spring Security to try other authentication methods
2. **Public Endpoint Bypass**: Skips authentication for public endpoints like `/public/**` and `/actuator/**`
3. **Existing Authentication Check**: Avoids re-authentication if user is already authenticated

### SecurityConfig

Configures the security filter chain to support both LDAP and Windows authentication:

- Adds the custom `NegotiateSecurityFilter` before `BasicAuthenticationFilter`
- Configures Waffle security providers for Windows authentication
- Sets up LDAP context and authentication providers
- Provides flexible configuration via properties

## Configuration

Configure the application using `application.properties`:

```properties
# LDAP Configuration
ldap.url=ldap://your-ldap-server:389
ldap.domain=YOUR-DOMAIN.COM
ldap.root=dc=yourdomain,dc=com

# Waffle Configuration  
waffle.enabled=true

# Server Configuration
server.port=8080
```

## API Endpoints

- `GET /public/health` - Public health check endpoint
- `GET /user/info` - Returns authenticated user information
- `GET /secure` - Secure endpoint requiring authentication

## Building and Running

```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

## Troubleshooting

### 403 Errors with Waffle

The original issue was that adding the Waffle library caused 403 errors. This is resolved by:

1. Using the `CustomNegotiateSecurityFilter` that handles authentication failures gracefully
2. Proper configuration of the security filter chain
3. Fallback mechanisms when Windows authentication is not available

### Windows-Specific Requirements

Waffle requires Windows-specific libraries. On non-Windows environments:
- Set `waffle.enabled=false` to disable Windows authentication
- The application will fall back to LDAP-only authentication