FROM openjdk:17-jdk-slim

VOLUME /tmp

# Create app directory
WORKDIR /app

# Copy the executable JAR
COPY target/sso-spring-ldap-*.jar app.jar

# Copy configuration files
COPY src/main/resources/application*.yml ./

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]