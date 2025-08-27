package com.recipe_manager.model.dto.external.mediamanager.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.model.enums.HealthStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class HealthResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(5)
            .build())
        .storage(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(3)
            .build())
        .overall(HealthStatus.HEALTHY)
        .build();

    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .timestamp(timestamp)
        .service("media-management-service")
        .version("0.1.0")
        .responseTimeMs(25)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getTimestamp()).isEqualTo(timestamp);
    assertThat(dto.getService()).isEqualTo("media-management-service");
    assertThat(dto.getVersion()).isEqualTo("0.1.0");
    assertThat(dto.getResponseTimeMs()).isEqualTo(25);
    assertThat(dto.getChecks()).isNotNull();
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(HealthStatus.HEALTHY);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    LocalDateTime timestamp = LocalDateTime.of(2024, 2, 20, 14, 45, 30);

    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder()
            .status(HealthStatus.UNHEALTHY)
            .responseTimeMs(2000)
            .build())
        .storage(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(3)
            .build())
        .overall(HealthStatus.DEGRADED)
        .build();

    HealthResponseDto original = HealthResponseDto.builder()
        .status(HealthStatus.DEGRADED)
        .timestamp(timestamp)
        .service("media-management-service")
        .version("0.2.0")
        .responseTimeMs(2050)
        .checks(checks)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"status\":\"DEGRADED\"");
    assertThat(json).contains("\"service\":\"media-management-service\"");
    assertThat(json).contains("\"version\":\"0.2.0\"");
    assertThat(json).contains("\"response_time_ms\":2050");
    assertThat(json).contains("\"checks\"");
    assertThat(json).contains("\"database\"");
    assertThat(json).contains("\"storage\"");
    assertThat(json).contains("\"overall\":\"DEGRADED\"");

    HealthResponseDto deserialized = objectMapper.readValue(json, HealthResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle healthy status scenario")
  void shouldHandleHealthyStatusScenario() {
    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(5)
            .build())
        .storage(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(3)
            .build())
        .overall(HealthStatus.HEALTHY)
        .build();

    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(HealthStatus.HEALTHY);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle degraded status scenario")
  void shouldHandleDegradedStatusScenario() {
    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder()
            .status(HealthStatus.UNHEALTHY)
            .responseTimeMs(2000)
            .build())
        .storage(DependencyCheckDto.builder()
            .status(HealthStatus.HEALTHY)
            .responseTimeMs(3)
            .build())
        .overall(HealthStatus.DEGRADED)
        .build();

    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.DEGRADED)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(2050)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.DEGRADED);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(HealthStatus.DEGRADED);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle unhealthy status scenario")
  void shouldHandleUnhealthyStatusScenario() {
    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder()
            .status(HealthStatus.TIMEOUT)
            .responseTimeMs(2000)
            .build())
        .storage(DependencyCheckDto.builder()
            .status(HealthStatus.UNHEALTHY)
            .responseTimeMs(2000)
            .build())
        .overall(HealthStatus.UNHEALTHY)
        .build();

    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.UNHEALTHY)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(4010)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(HealthStatus.TIMEOUT);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(HealthStatus.UNHEALTHY);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .service("test-service")
        .version("1.0.0")
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getService()).isEqualTo("test-service");
    assertThat(dto.getVersion()).isEqualTo("1.0.0");
    assertThat(dto.getTimestamp()).isNull();
    assertThat(dto.getResponseTimeMs()).isNull();
    assertThat(dto.getChecks()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime time = LocalDateTime.of(2024, 6, 1, 12, 0, 0);
    HealthResponseDto.HealthChecksDto checks = HealthResponseDto.HealthChecksDto.builder()
        .database(DependencyCheckDto.builder().status(HealthStatus.HEALTHY).responseTimeMs(5).build())
        .storage(DependencyCheckDto.builder().status(HealthStatus.HEALTHY).responseTimeMs(3).build())
        .overall(HealthStatus.HEALTHY)
        .build();

    HealthResponseDto dto1 = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .timestamp(time)
        .service("service-1")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    HealthResponseDto dto2 = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .timestamp(time)
        .service("service-1")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    HealthResponseDto differentDto = HealthResponseDto.builder()
        .status(HealthStatus.DEGRADED)
        .timestamp(time.plusMinutes(1))
        .service("service-2")
        .version("2.0.0")
        .responseTimeMs(100)
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    HealthResponseDto dto = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .timestamp(LocalDateTime.of(2024, 12, 25, 10, 0, 0))
        .service("media-management-service")
        .version("2.1.0")
        .responseTimeMs(15)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("HealthResponseDto");
    assertThat(toString).contains("HEALTHY");
    assertThat(toString).contains("media-management-service");
    assertThat(toString).contains("2.1.0");
  }
}
