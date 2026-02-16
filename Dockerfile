# ---- Build Stage ----
FROM maven:3-eclipse-temurin-25@sha256:b8187abd63cd4ee8c596aae910ce698a10db6d27ad5be08d574f3b928526724e AS build
WORKDIR /app

# Copy only pom.xml and checkstyle.xml first for dependency caching
COPY pom.xml .
COPY checkstyle.xml .

# Download dependencies in a separate layer (cached unless pom.xml changes)
# This helps with DNS timeouts and speeds up rebuilds
RUN mvn dependency:go-offline -B || mvn dependency:resolve -B

# Now copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:25.0.1_8-jre@sha256:9d1d3068b16f2c4127be238ca06439012ff14a8fdf38f8f62472160f9058464a
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
