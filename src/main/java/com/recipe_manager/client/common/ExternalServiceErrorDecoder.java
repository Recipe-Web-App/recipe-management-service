package com.recipe_manager.client.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.exception.ExternalServiceTimeoutException;
import com.recipe_manager.model.enums.ExternalServiceName;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Custom error decoder for Feign clients. Maps HTTP error responses to appropriate exception types
 * for consistent error handling across all external service integrations.
 */
public final class ExternalServiceErrorDecoder implements ErrorDecoder {

  /** Default timeout in milliseconds for timeout exceptions. */
  private static final long DEFAULT_TIMEOUT_MS = 5000L;

  /** Logger for error reporting and debugging. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalServiceErrorDecoder.class);

  /**
   * Decodes HTTP error responses into appropriate exception types.
   *
   * @param methodKey the method being called
   * @param response the HTTP response
   * @return appropriate exception based on status code
   */
  @Override
  public Exception decode(final String methodKey, final Response response) {
    ExternalServiceName serviceName = extractServiceNameFromMethodKey(methodKey);
    int status = response.status();
    String responseBody = extractResponseBody(response);

    if (status == HttpStatus.REQUEST_TIMEOUT.value()
        || status == HttpStatus.GATEWAY_TIMEOUT.value()) {
      return new ExternalServiceTimeoutException(serviceName, DEFAULT_TIMEOUT_MS);
    }

    if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
      return new ExternalServiceException(
          serviceName, status, "Rate limit exceeded for " + serviceName);
    }

    if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        || status == HttpStatus.BAD_GATEWAY.value()
        || status == HttpStatus.SERVICE_UNAVAILABLE.value()) {
      return new ExternalServiceException(
          serviceName, status, "Server error in " + serviceName + ": " + responseBody);
    }

    if (status == HttpStatus.BAD_REQUEST.value()
        || status == HttpStatus.NOT_FOUND.value()
        || status == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
      return new ExternalServiceException(
          serviceName, status, "Client error for " + serviceName + ": " + responseBody);
    }

    return new ExternalServiceException(
        serviceName, status, "Unexpected error from " + serviceName + ": " + responseBody);
  }

  private ExternalServiceName extractServiceNameFromMethodKey(final String methodKey) {
    if (methodKey.contains("RecipeScraper")) {
      return ExternalServiceName.RECIPE_SCRAPER;
    }
    // Default fallback - could be enhanced to parse from method key more intelligently
    return ExternalServiceName.RECIPE_SCRAPER;
  }

  private String extractResponseBody(final Response response) {
    try {
      if (response.body() != null) {
        InputStream inputStream = response.body().asInputStream();
        return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      // Log error but don't fail - return empty string
      LOGGER.warn("Failed to extract response body: {}", e.getMessage());
    }
    return "";
  }
}
