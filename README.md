# Corporate SSO Portal - Spring Boot LDAP Authentication

A comprehensive Single Sign-On (SSO) solution built with Spring Boot 3 and Spring Security 6, designed for corporate environments using Active Directory LDAP authentication.

![Login Page](https://github.com/user-attachments/assets/90e29507-7869-49b4-b657-e88d7fab4f09)

## Features

### 🔐 Authentication & Security
- **Spring Security 6** with LDAP authentication
- **Active Directory integration** for corporate environments
- **Role-based access control** (ROLE_USER, ROLE_ADMIN)
- **Configurable admin groups** via application properties
- **Session management** with secure logout functionality
- **Windows Authentication ready** (requires additional setup)

### 🎨 User Interface
- **Responsive design** that works on desktop, tablet, and mobile devices
- **Modern CSS** with CSS Grid and Flexbox layouts
- **Professional header layout** with logo, title, and user information
- **Clean, corporate-friendly design** with gradient backgrounds
- **Accessible forms** with proper labels and focus states

### 📱 Application Pages
- **Login Page**: Beautiful centered form with corporate branding
- **Home Dashboard**: User welcome with quick actions and role information
- **Profile Page**: Detailed user information from Active Directory
- **Admin Panel**: Administrative functions for privileged users
- **Error Pages**: User-friendly error handling

### ⚙️ Configuration Features
- Environment-specific application properties
- LDAP server configuration for Active Directory
- Customizable branding (logo text, application title)
- Admin group mapping for role assignment

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Access to Active Directory LDAP server

### Running the Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/rajsubbiah/sso-spring-ldap.git
   cd sso-spring-ldap
   ```

2. **Configure LDAP settings**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # Active Directory Configuration
   ad.domain=your-company.com
   ad.url=ldap://your-domain-controller:389
   ad.root-dn=DC=your-company,DC=com
   
   # LDAP Service Account
   ldap.username=cn=ldap-service,ou=Service Accounts,dc=your-company,dc=com
   ldap.password=your-ldap-service-password
   
   # Admin Groups (comma-separated)
   app.admin-groups=Domain Admins,Application Admins,IT Administrators
   
   # Application Branding
   app.title=Your Corporate Portal
   app.logo-text=YourCompany
   ```

3. **Build and run**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **Access the application**
   
   Open your browser and navigate to: `http://localhost:8080`

### Testing

Run the test suite:
```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/ssoldap/
│   │   ├── SsoLdapApplication.java              # Main application class
│   │   ├── config/
│   │   │   ├── SecurityConfig.java              # Security configuration
│   │   │   └── CustomUserDetailsContextMapper.java # User mapping
│   │   ├── controller/
│   │   │   └── HomeController.java              # Web controllers
│   │   ├── model/
│   │   │   └── CustomUserDetails.java          # User model
│   │   └── service/
│   │       └── ActiveDirectoryUserService.java # AD integration
│   └── resources/
│       ├── application.properties               # Configuration
│       ├── static/css/
│       │   └── style.css                       # Responsive CSS
│       └── templates/                          # Thymeleaf templates
│           ├── login.html                      # Login page
│           ├── home.html                       # Dashboard
│           ├── profile.html                    # User profile
│           ├── admin.html                      # Admin panel
│           └── error.html                      # Error page
└── test/
    └── java/com/example/ssoldap/
        └── SsoLdapApplicationTests.java        # Tests
```

## Configuration Options

### LDAP Configuration
```properties
# LDAP Server
ldap.url=ldap://domain-controller.company.com:389
ldap.base=DC=company,DC=com
ldap.username=cn=ldap-service,ou=Service Accounts,dc=company,dc=com
ldap.password=your-ldap-service-password

# Active Directory
ad.domain=company.com
ad.url=ldap://domain-controller.company.com:389
ad.root-dn=DC=company,DC=com
ad.user-search-filter=(&(objectClass=user)(sAMAccountName={0}))
```

### Application Configuration
```properties
# Server
server.port=8080

# Admin Groups
app.admin-groups=Domain Admins,Application Admins,IT Administrators

# Branding
app.title=Corporate SSO Portal
app.logo-text=MyCompany

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.ldap=DEBUG
```

## Security Features

### Authentication Flow
1. User accesses protected resource
2. Redirected to login page if not authenticated
3. Credentials validated against Active Directory
4. User details fetched from LDAP
5. Roles assigned based on AD group membership
6. Session established with appropriate permissions

### Role-Based Access Control
- **ROLE_USER**: Default role for all authenticated users
- **ROLE_ADMIN**: Assigned to users in configured admin groups
- **Method-level security**: `@PreAuthorize` annotations supported
- **URL-level security**: Admin endpoints protected

### Session Management
- Configurable session timeout
- Secure logout with session invalidation
- Remember-me functionality (optional)
- CSRF protection enabled

## Deployment Considerations

### Production Setup
1. **SSL/TLS Configuration**
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=your-password
   ```

2. **LDAP Security**
   - Use LDAPS (LDAP over SSL) for production
   - Configure connection pooling
   - Set up service account with minimal permissions

3. **Application Security**
   - Configure session timeout
   - Enable security headers
   - Set up monitoring and logging

### Environment Variables
For sensitive configuration, use environment variables:
```bash
export LDAP_PASSWORD=your-secure-password
export AD_DOMAIN=your-company.com
export AD_URL=ldaps://secure-dc.your-company.com:636
```

## Windows Authentication

For automatic Windows authentication on corporate networks, additional setup is required:

1. Add Windows Authentication libraries (requires corporate environment)
2. Configure Kerberos authentication
3. Set up domain trust relationships

## Troubleshooting

### Common Issues

**LDAP Connection Failed**
- Verify LDAP server URL and port
- Check network connectivity
- Validate service account credentials

**User Not Found**
- Verify user search filter
- Check LDAP base DN configuration
- Ensure user exists in specified OU

**Access Denied**
- Check admin group configuration
- Verify user group membership in AD
- Review Spring Security logs

### Logging
Enable debug logging for troubleshooting:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.ldap=DEBUG
logging.level.com.example.ssoldap=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Check the troubleshooting section
- Review Spring Security and Spring LDAP documentation