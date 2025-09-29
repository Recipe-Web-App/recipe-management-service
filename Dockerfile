# ---- Build Stage ----
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl for health checks
RUN apt-get update \
  && apt-get install -y curl --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN useradd -m spring && mkdir -p /app && chown -R spring:spring /app

# Copy the built jar from the build stage
COPY --from=build /app/target/recipe-management-service-*.jar app.jar

# Use a non-root user
USER spring

# Expose the default Spring Boot port
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV JAVA_OPTS=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
