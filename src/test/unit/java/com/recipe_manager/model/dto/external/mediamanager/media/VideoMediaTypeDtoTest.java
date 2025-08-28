package com.recipe_manager.model.dto.external.mediamanager.media;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.MediaFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class VideoMediaTypeDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(1920)
        .height(1080)
        .durationSeconds(120)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.MP4);
    assertThat(dto.getWidth()).isEqualTo(1920);
    assertThat(dto.getHeight()).isEqualTo(1080);
    assertThat(dto.getDurationSeconds()).isEqualTo(120);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    VideoMediaTypeDto original = VideoMediaTypeDto.builder()
        .format(MediaFormat.WEBM)
        .width(1280)
        .height(720)
        .durationSeconds(300)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"format\":\"WEBM\"");
    assertThat(json).contains("\"width\":1280");
    assertThat(json).contains("\"height\":720");
    assertThat(json).contains("\"duration_seconds\":300");

    VideoMediaTypeDto deserialized = objectMapper.readValue(json, VideoMediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all video formats")
  void shouldSupportAllVideoFormats() {
    MediaFormat[] videoFormats = {
        MediaFormat.MP4,
        MediaFormat.WEBM,
        MediaFormat.MOV,
        MediaFormat.AVI
    };

    for (MediaFormat format : videoFormats) {
      VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
          .format(format)
          .width(1280)
          .height(720)
          .durationSeconds(60)
          .build();

      assertThat(dto.getFormat()).isEqualTo(format);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various video resolutions")
  void shouldHandleVariousVideoResolutions() {
    int[][] resolutions = {
        {640, 480},   // SD
        {1280, 720},  // HD
        {1920, 1080}, // Full HD
        {2560, 1440}, // 2K
        {3840, 2160}, // 4K
        {7680, 4320}  // 8K
    };

    for (int[] res : resolutions) {
      VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
          .format(MediaFormat.MP4)
          .width(res[0])
          .height(res[1])
          .durationSeconds(120)
          .build();

      assertThat(dto.getWidth()).isEqualTo(res[0]);
      assertThat(dto.getHeight()).isEqualTo(res[1]);
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null duration")
  void shouldHandleNullDuration() {
    VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(1920)
        .height(1080)
        .durationSeconds(null)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.MP4);
    assertThat(dto.getWidth()).isEqualTo(1920);
    assertThat(dto.getHeight()).isEqualTo(1080);
    assertThat(dto.getDurationSeconds()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various video durations")
  void shouldHandleVariousVideoDurations() {
    int[] durations = {
        0,      // 0 seconds
        10,     // 10 seconds
        60,     // 1 minute
        300,    // 5 minutes
        600,    // 10 minutes
        1800,   // 30 minutes
        3600,   // 1 hour
        7200,   // 2 hours
        10800   // 3 hours
    };

    for (int duration : durations) {
      VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
          .format(MediaFormat.MP4)
          .width(1280)
          .height(720)
          .durationSeconds(duration)
          .build();

      assertThat(dto.getDurationSeconds()).isEqualTo(duration);
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle all null values")
  void shouldHandleAllNullValues() {
    VideoMediaTypeDto dto = VideoMediaTypeDto.builder().build();

    assertThat(dto.getFormat()).isNull();
    assertThat(dto.getWidth()).isNull();
    assertThat(dto.getHeight()).isNull();
    assertThat(dto.getDurationSeconds()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle vertical videos")
  void shouldHandleVerticalVideos() {
    VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(720)
        .height(1280)
        .durationSeconds(30)
        .build();

    assertThat(dto.getWidth()).isLessThan(dto.getHeight());
    assertThat(dto.getWidth()).isEqualTo(720);
    assertThat(dto.getHeight()).isEqualTo(1280);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    VideoMediaTypeDto dto1 = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(1920)
        .height(1080)
        .durationSeconds(120)
        .build();

    VideoMediaTypeDto dto2 = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(1920)
        .height(1080)
        .durationSeconds(120)
        .build();

    VideoMediaTypeDto differentDto = VideoMediaTypeDto.builder()
        .format(MediaFormat.WEBM)
        .width(1280)
        .height(720)
        .durationSeconds(60)
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
    VideoMediaTypeDto dto = VideoMediaTypeDto.builder()
        .format(MediaFormat.MOV)
        .width(3840)
        .height(2160)
        .durationSeconds(600)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("VideoMediaTypeDto");
    assertThat(toString).contains("MOV");
    assertThat(toString).contains("3840");
    assertThat(toString).contains("2160");
    assertThat(toString).contains("600");
  }
}
