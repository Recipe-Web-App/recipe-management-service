package com.recipe_manager.model.dto.external.mediamanager.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.model.enums.ReadinessStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ReadinessResponseDtoTest {

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

    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(5)
            .build())
        .storage(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(3)
            .build())
        .overall(ReadinessStatus.READY)
        .build();

    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .timestamp(timestamp)
        .service("media-management-service")
        .version("0.1.0")
        .responseTimeMs(25)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getTimestamp()).isEqualTo(timestamp);
    assertThat(dto.getService()).isEqualTo("media-management-service");
    assertThat(dto.getVersion()).isEqualTo("0.1.0");
    assertThat(dto.getResponseTimeMs()).isEqualTo(25);
    assertThat(dto.getChecks()).isNotNull();
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(ReadinessStatus.READY);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    LocalDateTime timestamp = LocalDateTime.of(2024, 3, 10, 16, 20, 45);

    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.TIMEOUT)
            .responseTimeMs(2000)
            .build())
        .storage(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(3)
            .build())
        .overall(ReadinessStatus.NOT_READY)
        .build();

    ReadinessResponseDto original = ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .timestamp(timestamp)
        .service("media-management-service")
        .version("0.3.0")
        .responseTimeMs(2010)
        .checks(checks)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"status\":\"NOT_READY\"");
    assertThat(json).contains("\"service\":\"media-management-service\"");
    assertThat(json).contains("\"version\":\"0.3.0\"");
    assertThat(json).contains("\"response_time_ms\":2010");
    assertThat(json).contains("\"checks\"");
    assertThat(json).contains("\"database\"");
    assertThat(json).contains("\"storage\"");
    assertThat(json).contains("\"overall\":\"NOT_READY\"");

    ReadinessResponseDto deserialized = objectMapper.readValue(json, ReadinessResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle ready status scenario")
  void shouldHandleReadyStatusScenario() {
    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(5)
            .build())
        .storage(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(3)
            .build())
        .overall(ReadinessStatus.READY)
        .build();

    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(ReadinessStatus.READY);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle not ready status scenario")
  void shouldHandleNotReadyStatusScenario() {
    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.NOT_READY)
            .responseTimeMs(1500)
            .build())
        .storage(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.READY)
            .responseTimeMs(3)
            .build())
        .overall(ReadinessStatus.NOT_READY)
        .build();

    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(1510)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getChecks().getOverall()).isEqualTo(ReadinessStatus.NOT_READY);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle timeout scenario")
  void shouldHandleTimeoutScenario() {
    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.TIMEOUT)
            .responseTimeMs(2000)
            .build())
        .storage(ReadinessDependencyCheckDto.builder()
            .status(ReadinessStatus.TIMEOUT)
            .responseTimeMs(2000)
            .build())
        .overall(ReadinessStatus.NOT_READY)
        .build();

    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .timestamp(LocalDateTime.now())
        .service("media-management-service")
        .version("1.0.0")
        .responseTimeMs(4010)
        .checks(checks)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(dto.getChecks().getDatabase().getStatus()).isEqualTo(ReadinessStatus.TIMEOUT);
    assertThat(dto.getChecks().getStorage().getStatus()).isEqualTo(ReadinessStatus.TIMEOUT);
    assertThat(dto.getChecks().getOverall()).isEqualTo(ReadinessStatus.NOT_READY);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .service("test-service")
        .version("1.0.0")
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getService()).isEqualTo("test-service");
    assertThat(dto.getVersion()).isEqualTo("1.0.0");
    assertThat(dto.getTimestamp()).isNull();
    assertThat(dto.getResponseTimeMs()).isNull();
    assertThat(dto.getChecks()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should demonstrate binary nature of readiness")
  void shouldDemonstrateBinaryNatureOfReadiness() {
    // Ready: ALL dependencies must be ready
    ReadinessResponseDto readyDto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .checks(ReadinessResponseDto.ReadinessChecksDto.builder()
            .database(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.READY).responseTimeMs(5).build())
            .storage(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.READY).responseTimeMs(3).build())
            .overall(ReadinessStatus.READY)
            .build())
        .build();

    // Not Ready: ANY dependency failure causes NOT_READY
    ReadinessResponseDto notReadyDto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .checks(ReadinessResponseDto.ReadinessChecksDto.builder()
            .database(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.READY).responseTimeMs(5).build())
            .storage(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.NOT_READY).responseTimeMs(1500).build())
            .overall(ReadinessStatus.NOT_READY)
            .build())
        .build();

    assertThat(readyDto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(notReadyDto.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime time = LocalDateTime.of(2024, 8, 1, 9, 15, 30);
    ReadinessResponseDto.ReadinessChecksDto checks = ReadinessResponseDto.ReadinessChecksDto.builder()
        .database(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.READY).responseTimeMs(5).build())
        .storage(ReadinessDependencyCheckDto.builder().status(ReadinessStatus.READY).responseTimeMs(3).build())
        .overall(ReadinessStatus.READY)
        .build();

    ReadinessResponseDto dto1 = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .timestamp(time)
        .service("service-1")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    ReadinessResponseDto dto2 = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .timestamp(time)
        .service("service-1")
        .version("1.0.0")
        .responseTimeMs(10)
        .checks(checks)
        .build();

    ReadinessResponseDto differentDto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
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
    ReadinessResponseDto dto = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .timestamp(LocalDateTime.of(2024, 11, 5, 14, 22, 18))
        .service("media-management-service")
        .version("3.0.1")
        .responseTimeMs(12)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("ReadinessResponseDto");
    assertThat(toString).contains("READY");
    assertThat(toString).contains("media-management-service");
    assertThat(toString).contains("3.0.1");
  }
}
