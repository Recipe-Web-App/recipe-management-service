package com.recipe_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Configuration for observability and monitoring. Provides custom metrics for external service
 * calls and business operations.
 */
@Configuration
public class ObservabilityConfig {

  /**
   * Counter for external service calls.
   *
   * @param meterRegistry the meter registry
   * @return counter for external service calls
   */
  @Bean
  public Counter externalServiceCallsCounter(final MeterRegistry meterRegistry) {
    return Counter.builder("external.service.calls")
        .description("Total number of external service calls")
        .tag("service", "all")
        .register(meterRegistry);
  }

  /**
   * Counter for external service failures.
   *
   * @param meterRegistry the meter registry
   * @return counter for external service failures
   */
  @Bean
  public Counter externalServiceFailuresCounter(final MeterRegistry meterRegistry) {
    return Counter.builder("external.service.failures")
        .description("Total number of external service failures")
        .tag("service", "all")
        .register(meterRegistry);
  }

  /**
   * Timer for external service response times.
   *
   * @param meterRegistry the meter registry
   * @return timer for external service response times
   */
  @Bean
  public Timer externalServiceResponseTimer(final MeterRegistry meterRegistry) {
    return Timer.builder("external.service.response.time")
        .description("Response time for external service calls")
        .tag("service", "all")
        .register(meterRegistry);
  }

  /**
   * Counter for shopping list generations.
   *
   * @param meterRegistry the meter registry
   * @return counter for shopping list generations
   */
  @Bean
  public Counter shoppingListGenerationsCounter(final MeterRegistry meterRegistry) {
    return Counter.builder("shopping.list.generations")
        .description("Total number of shopping list generations")
        .register(meterRegistry);
  }

  /**
   * Counter for shopping lists with pricing data.
   *
   * @param meterRegistry the meter registry
   * @return counter for shopping lists with pricing data
   */
  @Bean
  public Counter shoppingListsWithPricingCounter(final MeterRegistry meterRegistry) {
    return Counter.builder("shopping.list.with.pricing")
        .description("Total number of shopping lists generated with pricing data")
        .register(meterRegistry);
  }
}
