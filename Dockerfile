FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests && \
    cp target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]