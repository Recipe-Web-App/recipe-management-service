package com.recipe_manager.model.dto.external.mediamanager.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.ReadinessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ReadinessDependencyCheckDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(5)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(5);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    ReadinessDependencyCheckDto original = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .responseTimeMs(1500)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"status\":\"NOT_READY\"");
    assertThat(json).contains("\"response_time_ms\":1500");

    ReadinessDependencyCheckDto deserialized = objectMapper.readValue(json, ReadinessDependencyCheckDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support ready status")
  void shouldSupportReadyStatus() {
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(3)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(3);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should support not ready status")
  void shouldSupportNotReadyStatus() {
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .responseTimeMs(1200)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(1200);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should support timeout status")
  void shouldSupportTimeoutStatus() {
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.TIMEOUT)
        .responseTimeMs(2000)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ReadinessStatus.TIMEOUT);
    assertThat(dto.getResponseTimeMs()).isEqualTo(2000);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various response times")
  void shouldHandleVariousResponseTimes() {
    int[] responseTimes = {
        0,      // Instant response
        1,      // 1ms - very fast
        5,      // 5ms - fast
        10,     // 10ms - good
        50,     // 50ms - acceptable
        100,    // 100ms - slower but acceptable
        500,    // 500ms - slow
        1000,   // 1 second - threshold
        2000    // 2 seconds - timeout
    };

    for (int responseTime : responseTimes) {
      ReadinessStatus expectedStatus;
      if (responseTime >= 2000) {
        expectedStatus = ReadinessStatus.TIMEOUT;
      } else if (responseTime > 1000) {
        expectedStatus = ReadinessStatus.NOT_READY;
      } else {
        expectedStatus = ReadinessStatus.READY;
      }

      ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
          .status(expectedStatus)
          .responseTimeMs(responseTime)
          .build();

      assertThat(dto.getResponseTimeMs()).isEqualTo(responseTime);
      assertThat(dto.getStatus()).isEqualTo(expectedStatus);
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values")
  void shouldHandleNullValues() {
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder().build();

    assertThat(dto.getStatus()).isNull();
    assertThat(dto.getResponseTimeMs()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support database readiness check")
  void shouldSupportDatabaseReadinessCheck() {
    ReadinessDependencyCheckDto databaseCheck = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(8)
        .build();

    assertThat(databaseCheck.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(databaseCheck.getResponseTimeMs()).isEqualTo(8);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support storage readiness check")
  void shouldSupportStorageReadinessCheck() {
    ReadinessDependencyCheckDto storageCheck = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(3)
        .build();

    assertThat(storageCheck.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(storageCheck.getResponseTimeMs()).isEqualTo(3);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should demonstrate binary nature of readiness checks")
  void shouldDemonstrateBinaryNatureOfReadinessChecks() {
    // Fast response - READY
    ReadinessDependencyCheckDto fastCheck = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(5)
        .build();

    // Slow response - NOT_READY
    ReadinessDependencyCheckDto slowCheck = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .responseTimeMs(1500)
        .build();

    // Timeout response - TIMEOUT (which results in NOT_READY overall)
    ReadinessDependencyCheckDto timeoutCheck = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.TIMEOUT)
        .responseTimeMs(2000)
        .build();

    assertThat(fastCheck.getStatus()).isEqualTo(ReadinessStatus.READY);
    assertThat(slowCheck.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(timeoutCheck.getStatus()).isEqualTo(ReadinessStatus.TIMEOUT);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    ReadinessDependencyCheckDto dto1 = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(10)
        .build();

    ReadinessDependencyCheckDto dto2 = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.READY)
        .responseTimeMs(10)
        .build();

    ReadinessDependencyCheckDto differentDto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .responseTimeMs(1500)
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
    ReadinessDependencyCheckDto dto = ReadinessDependencyCheckDto.builder()
        .status(ReadinessStatus.TIMEOUT)
        .responseTimeMs(2000)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("ReadinessDependencyCheckDto");
    assertThat(toString).contains("TIMEOUT");
    assertThat(toString).contains("2000");
  }
}
