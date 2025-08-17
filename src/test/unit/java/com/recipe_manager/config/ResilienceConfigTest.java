package com.recipe_manager.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ResilienceConfig.class)
@TestPropertySource(properties = {
    "resilience.circuitBreaker.failureRateThreshold=50.0",
    "resilience.circuitBreaker.waitDurationInOpenState=PT10S",
    "resilience.circuitBreaker.slidingWindowSize=10",
    "resilience.circuitBreaker.minimumNumberOfCalls=5",
    "resilience.circuitBreaker.permittedNumberOfCallsInHalfOpenState=3",
    "resilience.retry.maxAttempts=3",
    "resilience.retry.waitDuration=PT1S"
})
class ResilienceConfigTest {

  @Autowired
  private ResilienceConfig config;

  @Test
  @DisplayName("Should load circuit breaker configuration")
  void shouldLoadCircuitBreakerConfig() {
    ResilienceConfig.CircuitBreakerConfig cbConfig = config.getCircuitBreaker();

    assertThat(cbConfig.getFailureRateThreshold()).isEqualTo(50.0f);
    assertThat(cbConfig.getWaitDurationInOpenState()).isEqualTo(Duration.ofSeconds(10));
    assertThat(cbConfig.getSlidingWindowSize()).isEqualTo(10);
    assertThat(cbConfig.getMinimumNumberOfCalls()).isEqualTo(5);
    assertThat(cbConfig.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should load retry configuration")
  void shouldLoadRetryConfig() {
    ResilienceConfig.RetryConfig retryConfig = config.getRetry();

    assertThat(retryConfig.getMaxRetryAttempts()).isEqualTo(3);
    assertThat(retryConfig.getWaitDuration()).isEqualTo(Duration.ofSeconds(1));
  }

  @Test
  @DisplayName("Should validate circuit breaker thresholds")
  void shouldValidateCircuitBreakerThresholds() {
    ResilienceConfig.CircuitBreakerConfig cbConfig = config.getCircuitBreaker();

    assertThat(cbConfig.getFailureRateThreshold())
        .isGreaterThan(0.0f)
        .isLessThanOrEqualTo(100.0f);
    assertThat(cbConfig.getSlidingWindowSize())
        .isGreaterThan(0);
    assertThat(cbConfig.getMinimumNumberOfCalls())
        .isGreaterThan(0);
  }

  @Test
  @DisplayName("Should validate retry configuration")
  void shouldValidateRetryConfig() {
    ResilienceConfig.RetryConfig retryConfig = config.getRetry();

    assertThat(retryConfig.getMaxRetryAttempts())
        .isGreaterThan(0);
    assertThat(retryConfig.getWaitDuration())
        .isGreaterThan(Duration.ZERO);
  }
}
