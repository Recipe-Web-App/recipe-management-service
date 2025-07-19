# ---- Build Stage ----
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user
RUN useradd -m spring && mkdir -p /app && chown -R spring:spring /app

# Copy the built jar from the build stage
COPY --from=build /app/target/recipe-manager-service-*.jar app.jar

# Use a non-root user
USER spring

# Expose the default Spring Boot port
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
