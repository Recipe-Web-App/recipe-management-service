package com.recipe_manager.unit_tests.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.config.ObservabilityConfig;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObservabilityConfigTest {

  @Test
  @DisplayName("Should create external service calls counter")
  void shouldCreateExternalServiceCallsCounter() {
    ObservabilityConfig config = new ObservabilityConfig();
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    Counter counter = config.externalServiceCallsCounter(meterRegistry);

    assertThat(counter).isNotNull();
    assertThat(counter.getId().getName()).isEqualTo("external.service.calls");
  }

  @Test
  @DisplayName("Should create external service failures counter")
  void shouldCreateExternalServiceFailuresCounter() {
    ObservabilityConfig config = new ObservabilityConfig();
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    Counter counter = config.externalServiceFailuresCounter(meterRegistry);

    assertThat(counter).isNotNull();
    assertThat(counter.getId().getName()).isEqualTo("external.service.failures");
  }

  @Test
  @DisplayName("Should create external service response timer")
  void shouldCreateExternalServiceResponseTimer() {
    ObservabilityConfig config = new ObservabilityConfig();
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    Timer timer = config.externalServiceResponseTimer(meterRegistry);

    assertThat(timer).isNotNull();
    assertThat(timer.getId().getName()).isEqualTo("external.service.response.time");
  }

  @Test
  @DisplayName("Should create shopping list generations counter")
  void shouldCreateShoppingListGenerationsCounter() {
    ObservabilityConfig config = new ObservabilityConfig();
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    Counter counter = config.shoppingListGenerationsCounter(meterRegistry);

    assertThat(counter).isNotNull();
    assertThat(counter.getId().getName()).isEqualTo("shopping.list.generations");
  }

  @Test
  @DisplayName("Should create shopping lists with pricing counter")
  void shouldCreateShoppingListsWithPricingCounter() {
    ObservabilityConfig config = new ObservabilityConfig();
    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    Counter counter = config.shoppingListsWithPricingCounter(meterRegistry);

    assertThat(counter).isNotNull();
    assertThat(counter.getId().getName()).isEqualTo("shopping.list.with.pricing");
  }
}
