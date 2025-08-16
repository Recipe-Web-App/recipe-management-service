package com.recipe_manager.unit_tests.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.model.enums.ExternalServiceName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ExternalServiceExceptionTest {

  @Test
  @DisplayName("Should create exception with service name and message")
  void shouldCreateExceptionWithServiceNameAndMessage() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        "Test error message");

    assertAll(
        () -> assertThat(exception.getMessage())
            .isEqualTo("Test error message"),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(0));
  }

  @Test
  @DisplayName("Should create exception with service name, status code, and message")
  void shouldCreateExceptionWithStatusCode() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        404,
        "Not Found");

    assertAll(
        () -> assertThat(exception.getMessage())
            .isEqualTo("Not Found"),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(404));
  }

  @Test
  @DisplayName("Should create exception with service name, message, and cause")
  void shouldCreateExceptionWithCause() {
    RuntimeException cause = new RuntimeException("Original error");
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        "Wrapped error",
        cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .isEqualTo("Wrapped error"),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(0));
  }

  @Test
  @DisplayName("Should create exception with service name, status code, message, and cause")
  void shouldCreateExceptionWithAllParameters() {
    RuntimeException cause = new RuntimeException("Root cause");
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        500,
        "Internal Server Error",
        cause);

    assertAll(
        () -> assertThat(exception.getMessage())
            .isEqualTo("Internal Server Error"),
        () -> assertThat(exception.getCause())
            .isEqualTo(cause),
        () -> assertThat(exception.getServiceName())
            .isEqualTo(ExternalServiceName.RECIPE_SCRAPER),
        () -> assertThat(exception.getStatusCode())
            .isEqualTo(500));
  }

  @Test
  @DisplayName("Should determine retryability for 5xx errors")
  void shouldBeRetryableFor5xxErrors() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        500,
        "Internal Server Error");

    assertThat(exception.isRetryable()).isTrue();
  }

  @Test
  @DisplayName("Should determine retryability for 429 Too Many Requests")
  void shouldBeRetryableFor429() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        429,
        "Too Many Requests");

    assertThat(exception.isRetryable()).isTrue();
  }

  @Test
  @DisplayName("Should determine retryability for zero status code")
  void shouldBeRetryableForZeroStatusCode() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        "Connection error");

    assertThat(exception.isRetryable()).isTrue();
  }

  @Test
  @DisplayName("Should not be retryable for 4xx client errors except 429")
  void shouldNotBeRetryableFor4xxErrors() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        404,
        "Not Found");

    assertThat(exception.isRetryable()).isFalse();
  }

  @Test
  @DisplayName("Should not be retryable for 3xx redirect errors")
  void shouldNotBeRetryableFor3xxErrors() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        301,
        "Moved Permanently");

    assertThat(exception.isRetryable()).isFalse();
  }

  @Test
  @DisplayName("Should not be retryable for 2xx success codes")
  void shouldNotBeRetryableFor2xxSuccess() {
    ExternalServiceException exception = new ExternalServiceException(
        ExternalServiceName.RECIPE_SCRAPER,
        200,
        "OK");

    assertThat(exception.isRetryable()).isFalse();
  }
}
