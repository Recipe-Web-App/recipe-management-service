package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class ActuatorKarateRunner {
  @Karate.Test
  @DisplayName("Health Endpoint")
  Karate testHealth() {
    return Karate.run("feature/actuator/health.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Info Endpoint")
  Karate testInfo() {
    return Karate.run("feature/actuator/info.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Metrics Endpoint")
  Karate testMetrics() {
    return Karate.run("feature/actuator/metrics.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Environment Endpoint")
  Karate testEnvironment() {
    return Karate.run("feature/actuator/env.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Configuration Properties Endpoint")
  Karate testConfigProps() {
    return Karate.run("feature/actuator/configprops.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Prometheus Metrics Endpoint")
  Karate testPrometheus() {
    return Karate.run("feature/actuator/prometheus.feature").relativeTo(getClass());
  }
}
