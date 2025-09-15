# SSO Spring LDAP with Waffle Authentication

This Spring Boot application demonstrates Windows authentication using the Waffle library to get the current logged-on user name.

## Features

- Windows authentication using Waffle Spring Boot Starter 3
- RESTful endpoints to retrieve current user information
- Automatic extraction of domain and username from Windows credentials
- Integration with Spring Security

## Dependencies

- Spring Boot 3.2.0
- Waffle Spring Boot Starter 3 (version 3.4.0)
- Spring Security
- Spring Web

## Endpoints

- `GET /` - Welcome page showing authenticated user
- `GET /api/user/current` - Detailed current user information
- `GET /api/user/info` - Simplified user information with domain/username extraction

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080. When accessed from a Windows environment with proper domain authentication, it will automatically authenticate the current Windows user and display their information.

## Configuration

The application is configured to use Windows authentication through the following properties in `application.properties`:

```properties
waffle.windows-auth.enabled=true
waffle.windows-auth.negotiate=true
waffle.windows-auth.ntlm=true
```

## Note

This application is designed to work in Windows environments with Active Directory authentication. The Waffle library provides seamless integration with Windows authentication mechanisms (NTLM, Negotiate/Kerberos).