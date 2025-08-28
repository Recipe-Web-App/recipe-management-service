package com.recipe_manager.model.dto.external.mediamanager.media;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.MediaFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ImageMediaTypeDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
        .format(MediaFormat.JPEG)
        .width(1920)
        .height(1080)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.JPEG);
    assertThat(dto.getWidth()).isEqualTo(1920);
    assertThat(dto.getHeight()).isEqualTo(1080);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    ImageMediaTypeDto original = ImageMediaTypeDto.builder()
        .format(MediaFormat.PNG)
        .width(800)
        .height(600)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"format\":\"PNG\"");
    assertThat(json).contains("\"width\":800");
    assertThat(json).contains("\"height\":600");

    ImageMediaTypeDto deserialized = objectMapper.readValue(json, ImageMediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all image formats")
  void shouldSupportAllImageFormats() {
    MediaFormat[] imageFormats = {
        MediaFormat.JPEG,
        MediaFormat.PNG,
        MediaFormat.WEBP,
        MediaFormat.AVIF,
        MediaFormat.GIF
    };

    for (MediaFormat format : imageFormats) {
      ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
          .format(format)
          .width(1024)
          .height(768)
          .build();

      assertThat(dto.getFormat()).isEqualTo(format);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various dimensions")
  void shouldHandleVariousDimensions() {
    int[][] dimensions = {
        {640, 480},   // VGA
        {800, 600},   // SVGA
        {1024, 768},  // XGA
        {1280, 720},  // HD
        {1920, 1080}, // Full HD
        {3840, 2160}, // 4K
        {7680, 4320}  // 8K
    };

    for (int[] dim : dimensions) {
      ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
          .format(MediaFormat.JPEG)
          .width(dim[0])
          .height(dim[1])
          .build();

      assertThat(dto.getWidth()).isEqualTo(dim[0]);
      assertThat(dto.getHeight()).isEqualTo(dim[1]);
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values")
  void shouldHandleNullValues() {
    ImageMediaTypeDto dto = ImageMediaTypeDto.builder().build();

    assertThat(dto.getFormat()).isNull();
    assertThat(dto.getWidth()).isNull();
    assertThat(dto.getHeight()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle portrait orientation")
  void shouldHandlePortraitOrientation() {
    ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
        .format(MediaFormat.JPEG)
        .width(1080)
        .height(1920)
        .build();

    assertThat(dto.getWidth()).isLessThan(dto.getHeight());
    assertThat(dto.getWidth()).isEqualTo(1080);
    assertThat(dto.getHeight()).isEqualTo(1920);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle square images")
  void shouldHandleSquareImages() {
    ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
        .format(MediaFormat.PNG)
        .width(1024)
        .height(1024)
        .build();

    assertThat(dto.getWidth()).isEqualTo(dto.getHeight());
    assertThat(dto.getWidth()).isEqualTo(1024);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    ImageMediaTypeDto dto1 = ImageMediaTypeDto.builder()
        .format(MediaFormat.WEBP)
        .width(1280)
        .height(720)
        .build();

    ImageMediaTypeDto dto2 = ImageMediaTypeDto.builder()
        .format(MediaFormat.WEBP)
        .width(1280)
        .height(720)
        .build();

    ImageMediaTypeDto differentDto = ImageMediaTypeDto.builder()
        .format(MediaFormat.GIF)
        .width(640)
        .height(480)
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
    ImageMediaTypeDto dto = ImageMediaTypeDto.builder()
        .format(MediaFormat.AVIF)
        .width(3840)
        .height(2160)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("ImageMediaTypeDto");
    assertThat(toString).contains("AVIF");
    assertThat(toString).contains("3840");
    assertThat(toString).contains("2160");
  }
}
