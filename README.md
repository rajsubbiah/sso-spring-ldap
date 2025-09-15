# SSO Spring LDAP - Active Directory Integration

A Spring Boot 3 application for retrieving user information from Active Directory (AD) using LDAP integration.

## Features

- 🔍 **User Information Retrieval**: Get comprehensive user details from Active Directory
- 🌐 **RESTful API**: Easy-to-use REST endpoints for user lookup
- 🔧 **Configurable**: Flexible configuration for different AD environments
- 📊 **Comprehensive User Data**: Retrieve username, email, groups, department, title, and more
- 🛡️ **Security**: Built with Spring Security best practices
- ⚡ **Spring Boot 3**: Latest Spring Boot framework with Java 17

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Access to an Active Directory server
- Service account credentials (optional, for authenticated binding)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd sso-spring-ldap
mvn clean install
```

### 2. Configure Active Directory Settings

Edit `src/main/resources/application.yml`:

```yaml
ldap:
  url: ldap://your-ad-server.domain.com:389
  base: dc=yourdomain,dc=com
  username: service-account@yourdomain.com  # Optional
  password: service-password                # Optional
  
  user:
    search-base: cn=Users,dc=yourdomain,dc=com
    search-filter: (sAMAccountName={0})
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Get User Information (Query Parameters)

```http
GET /api/v1/users/info?username=john.doe&domain=example.com
```

**Response:**
```json
{
  "username": "john.doe",
  "displayName": "John Doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "department": "IT",
  "title": "Software Engineer",
  "phone": "+1-555-0123",
  "distinguishedName": "CN=John Doe,CN=Users,DC=example,DC=com",
  "memberOf": [
    "CN=Domain Users,CN=Users,DC=example,DC=com",
    "CN=IT Department,CN=Users,DC=example,DC=com"
  ],
  "enabled": true
}
```

### Get User Information (Path Variables)

```http
GET /api/v1/users/info/example.com/john.doe
```

### Health Check

```http
GET /api/v1/users/health
```

## Configuration Options

### LDAP Configuration

| Property | Description | Default | Required |
|----------|-------------|---------|----------|
| `ldap.url` | LDAP server URL | `ldap://localhost:389` | Yes |
| `ldap.base` | Base DN for searches | - | Yes |
| `ldap.username` | Service account username | - | No |
| `ldap.password` | Service account password | - | No |
| `ldap.user.search-base` | User search base DN | - | No |
| `ldap.user.search-filter` | User search filter | `(sAMAccountName={0})` | No |

### Example Configurations

#### Basic Configuration (Anonymous Bind)
```yaml
ldap:
  url: ldap://ad.company.com:389
  base: dc=company,dc=com
  user:
    search-base: cn=Users,dc=company,dc=com
```

#### Authenticated Configuration
```yaml
ldap:
  url: ldap://ad.company.com:389
  base: dc=company,dc=com
  username: ldapservice@company.com
  password: ${LDAP_PASSWORD}
  user:
    search-base: ou=Employees,dc=company,dc=com
```

#### Secure LDAP (LDAPS)
```yaml
ldap:
  url: ldaps://ad.company.com:636
  base: dc=company,dc=com
```

## Usage Examples

### Using curl

```bash
# Get user information
curl -X GET "http://localhost:8080/api/v1/users/info?username=johndoe&domain=example.com"

# Using path parameters
curl -X GET "http://localhost:8080/api/v1/users/info/example.com/johndoe"

# Health check
curl -X GET "http://localhost:8080/api/v1/users/health"
```

### Using Java/Spring RestTemplate

```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/v1/users/info?username={username}&domain={domain}";
AdUserInfo userInfo = restTemplate.getForObject(url, AdUserInfo.class, "johndoe", "example.com");
```

## Active Directory Attributes Mapped

| AD Attribute | Java Field | Description |
|-------------|------------|-------------|
| `sAMAccountName` | `username` | User's login name |
| `displayName` | `displayName` | Full display name |
| `mail` | `email` | Primary email address |
| `givenName` | `firstName` | First name |
| `sn` | `lastName` | Last name |
| `department` | `department` | Department |
| `title` | `title` | Job title |
| `telephoneNumber` | `phone` | Phone number |
| `distinguishedName` | `distinguishedName` | Full LDAP DN |
| `memberOf` | `memberOf` | List of groups |
| `userAccountControl` | `enabled` | Account enabled status |

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- `200 OK`: User found successfully
- `400 Bad Request`: Invalid parameters
- `404 Not Found`: User not found
- `500 Internal Server Error`: Server or LDAP connection error

### Error Response Format

```json
{
  "error": "User not found",
  "message": "User not found: johndoe in domain: example.com",
  "timestamp": 1640995200000
}
```

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package
java -jar target/sso-spring-ldap-0.0.1-SNAPSHOT.jar
```

### Environment Variables

You can use environment variables for sensitive configuration:

```bash
export LDAP_URL=ldap://your-ad-server:389
export LDAP_BASE=dc=yourdomain,dc=com
export LDAP_USERNAME=service-account
export LDAP_PASSWORD=your-password

java -jar target/sso-spring-ldap-0.0.1-SNAPSHOT.jar
```

## Security Considerations

1. **Service Account**: Use a dedicated service account with minimal privileges
2. **LDAPS**: Use secure LDAP (LDAPS) in production environments
3. **Network**: Restrict network access to LDAP servers
4. **Credentials**: Store sensitive credentials as environment variables or in secure configuration management
5. **Logging**: Be careful not to log sensitive information

## Troubleshooting

### Common Issues

1. **Connection Refused**: Check LDAP server URL and port
2. **Authentication Failed**: Verify service account credentials
3. **User Not Found**: Check search base and filter configuration
4. **SSL Certificate Issues**: For LDAPS, ensure proper certificate configuration

### Debug Logging

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    com.example.ssoldap: DEBUG
    org.springframework.ldap: DEBUG
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For issues and questions, please create an issue in the GitHub repository.