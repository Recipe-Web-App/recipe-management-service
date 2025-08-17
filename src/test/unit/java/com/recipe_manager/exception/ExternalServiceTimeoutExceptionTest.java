package com.recipe_manager.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.recipe_manager.model.enums.ExternalServiceName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExternalServiceTimeoutExceptionTest {

  @Test
  @DisplayName("Should create timeout exception with service name and timeout duration")
  void shouldCreateTimeoutExceptionWithServiceNameAndDuration() {
    ExternalServiceTimeoutException exception = new ExternalServiceTimeoutException(
        ExternalServiceName.RECIPE_SCRAPER,
        5000L);

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("timed out after 5000 milliseconds"),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getTimeoutDuration())
            .isEqualTo(5000L));
  }

  @Test
  @DisplayName("Should create timeout exception with service name, timeout, and cause")
  void shouldCreateTimeoutExceptionWithCause() {
    RuntimeException cause = new RuntimeException("Connection timeout");
    ExternalServiceTimeoutException exception = new ExternalServiceTimeoutException(
        ExternalServiceName.RECIPE_SCRAPER,
        3000L,
        cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .contains("timed out after 3000 milliseconds"),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getTimeoutDuration())
            .isEqualTo(3000L));
  }
}
