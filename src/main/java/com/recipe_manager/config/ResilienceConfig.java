package com.recipe_manager.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Configuration properties for resilience patterns (circuit breaker, retry, rate limiter, etc.).
 * Provides centralized configuration for resilience4j components.
 */
@Configuration
@ConfigurationProperties(prefix = "resilience")
@org.springframework.boot.context.properties.EnableConfigurationProperties
@Validated
@Data
public class ResilienceConfig {

  /** Circuit breaker configuration. */
  @Valid @NotNull private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();

  /** Retry configuration. */
  @Valid @NotNull private RetryConfig retry = new RetryConfig();

  /** Rate limiter configuration. */
  @Valid @NotNull private RateLimiterConfig rateLimiter = new RateLimiterConfig();

  /** Bulkhead configuration. */
  @Valid @NotNull private BulkheadConfig bulkhead = new BulkheadConfig();

  @Data
  public static class CircuitBreakerConfig {
    /** Failure rate threshold (percentage). */
    @NotNull @Positive private Float failureRateThreshold;

    /** Wait duration in open state. */
    @NotNull private Duration waitDurationInOpenState;

    /** Sliding window size for failure rate calculation. */
    @Positive private int slidingWindowSize;

    /** Minimum number of calls to calculate failure rate. */
    @Positive private int minimumNumberOfCalls;

    /** Permitted number of calls in half-open state. */
    @Positive private int permittedNumberOfCallsInHalfOpenState;
  }

  @Data
  public static class RetryConfig {
    /** Maximum number of retry attempts. */
    @Positive private int maxRetryAttempts;

    /** Wait duration between retries. */
    @NotNull private Duration waitDuration;

    /** Exponential backoff multiplier. */
    @NotNull @Positive private Double exponentialBackoffMultiplier;

    /** Maximum wait duration. */
    @NotNull private Duration maxWaitDuration;
  }

  @Data
  public static class RateLimiterConfig {
    /** Limit for period. */
    @Positive private int limitForPeriod;

    /** Limit refresh period. */
    @NotNull private Duration limitRefreshPeriod;

    /** Timeout duration for acquiring permission. */
    @NotNull private Duration timeoutDuration;
  }

  @Data
  public static class BulkheadConfig {
    /** Maximum concurrent calls. */
    @Positive private int maxConcurrentCalls;

    /** Maximum wait duration. */
    @NotNull private Duration maxWaitDuration;
  }
}
