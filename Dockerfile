# Use Java 17 (Spring Boot compatible)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the app
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/*.jar"]