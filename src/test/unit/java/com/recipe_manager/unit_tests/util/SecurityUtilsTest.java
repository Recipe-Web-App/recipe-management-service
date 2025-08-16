package com.recipe_manager.unit_tests.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Tag("unit")
class SecurityUtilsTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should generate correlation ID with correct format")
  void shouldGenerateCorrelationIdWithCorrectFormat() {
    String correlationId = SecurityUtils.generateCorrelationId();

    assertThat(correlationId)
        .matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
        .hasSize(36);
  }

  @Test
  @DisplayName("Should generate unique correlation IDs")
  void shouldGenerateUniqueCorrelationIds() {
    Set<String> correlationIds = new HashSet<>();
    int numberOfIds = 1000;

    for (int i = 0; i < numberOfIds; i++) {
      correlationIds.add(SecurityUtils.generateCorrelationId());
    }

    assertThat(correlationIds).hasSize(numberOfIds);
  }

  @Test
  @DisplayName("Should generate thread-safe correlation IDs")
  void shouldGenerateThreadSafeCorrelationIds() throws InterruptedException {
    int numberOfThreads = 10;
    int idsPerThread = 1000;
    Set<String> correlationIds = new HashSet<>();
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    for (int i = 0; i < numberOfThreads; i++) {
      executor.submit(() -> {
        try {
          for (int j = 0; j < idsPerThread; j++) {
            String id = SecurityUtils.generateCorrelationId();
            synchronized (correlationIds) {
              correlationIds.add(id);
            }
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executor.shutdown();

    assertThat(correlationIds).hasSize(numberOfThreads * idsPerThread);
  }

  @Test
  @DisplayName("Should generate UUID format correlation ID")
  void shouldGenerateUuidFormatCorrelationId() {
    String correlationId = SecurityUtils.generateCorrelationId();

    assertThat(correlationId)
        .matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
  }

  @Test
  @DisplayName("Should get current user ID from security context")
  void shouldGetCurrentUserIdFromSecurityContext() {
    // Arrange
    UUID expectedUserId = UUID.randomUUID();
    Authentication auth = new UsernamePasswordAuthenticationToken(expectedUserId.toString(), "password");
    SecurityContextHolder.getContext().setAuthentication(auth);

    // Act
    UUID actualUserId = SecurityUtils.getCurrentUserId();

    // Assert
    assertThat(actualUserId).isEqualTo(expectedUserId);
  }

  @Test
  @DisplayName("Should throw IllegalStateException when no authentication present")
  void shouldThrowExceptionWhenNoAuthentication() {
    // Arrange - no authentication set
    SecurityContextHolder.clearContext();

    // Act & Assert
    IllegalStateException exception = assertThrows(IllegalStateException.class,
        SecurityUtils::getCurrentUserId);

    assertThat(exception.getMessage()).contains("No authenticated user found in security context");
  }

  @Test
  @DisplayName("Should throw IllegalStateException when authentication name is null")
  void shouldThrowExceptionWhenAuthenticationNameIsNull() {
    // Arrange
    Authentication auth = new UsernamePasswordAuthenticationToken(null, "password");
    SecurityContextHolder.getContext().setAuthentication(auth);

    // Act & Assert
    IllegalStateException exception = assertThrows(IllegalStateException.class,
        SecurityUtils::getCurrentUserId);

    // The implementation actually catches this case and throws UUID parsing error
    assertThat(exception.getMessage()).contains("Authenticated user ID is not a valid UUID");
  }

  @Test
  @DisplayName("Should throw IllegalStateException when user ID is not valid UUID")
  void shouldThrowExceptionWhenUserIdIsNotValidUuid() {
    // Arrange
    Authentication auth = new UsernamePasswordAuthenticationToken("not-a-uuid", "password");
    SecurityContextHolder.getContext().setAuthentication(auth);

    // Act & Assert
    IllegalStateException exception = assertThrows(IllegalStateException.class,
        SecurityUtils::getCurrentUserId);

    assertThat(exception.getMessage()).contains("Authenticated user ID is not a valid UUID");
    assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
  }
}
