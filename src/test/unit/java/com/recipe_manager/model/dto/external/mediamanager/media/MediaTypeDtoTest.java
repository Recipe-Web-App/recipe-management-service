package com.recipe_manager.model.dto.external.mediamanager.media;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.MediaFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class MediaTypeDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO with image type")
  void shouldCreateDtoWithImageType() {
    ImageMediaTypeDto imageType = ImageMediaTypeDto.builder()
        .format(MediaFormat.JPEG)
        .width(1920)
        .height(1080)
        .build();

    MediaTypeDto dto = MediaTypeDto.builder()
        .image(imageType)
        .build();

    assertThat(dto.getImage()).isNotNull();
    assertThat(dto.getImage().getFormat()).isEqualTo(MediaFormat.JPEG);
    assertThat(dto.getImage().getWidth()).isEqualTo(1920);
    assertThat(dto.getImage().getHeight()).isEqualTo(1080);
    assertThat(dto.getVideo()).isNull();
    assertThat(dto.getAudio()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO with video type")
  void shouldCreateDtoWithVideoType() {
    VideoMediaTypeDto videoType = VideoMediaTypeDto.builder()
        .format(MediaFormat.MP4)
        .width(1280)
        .height(720)
        .durationSeconds(120)
        .build();

    MediaTypeDto dto = MediaTypeDto.builder()
        .video(videoType)
        .build();

    assertThat(dto.getVideo()).isNotNull();
    assertThat(dto.getVideo().getFormat()).isEqualTo(MediaFormat.MP4);
    assertThat(dto.getVideo().getWidth()).isEqualTo(1280);
    assertThat(dto.getVideo().getHeight()).isEqualTo(720);
    assertThat(dto.getVideo().getDurationSeconds()).isEqualTo(120);
    assertThat(dto.getImage()).isNull();
    assertThat(dto.getAudio()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO with audio type")
  void shouldCreateDtoWithAudioType() {
    AudioMediaTypeDto audioType = AudioMediaTypeDto.builder()
        .format(MediaFormat.MP3)
        .durationSeconds(240)
        .bitrate(128000)
        .build();

    MediaTypeDto dto = MediaTypeDto.builder()
        .audio(audioType)
        .build();

    assertThat(dto.getAudio()).isNotNull();
    assertThat(dto.getAudio().getFormat()).isEqualTo(MediaFormat.MP3);
    assertThat(dto.getAudio().getDurationSeconds()).isEqualTo(240);
    assertThat(dto.getAudio().getBitrate()).isEqualTo(128000);
    assertThat(dto.getImage()).isNull();
    assertThat(dto.getVideo()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize image type")
  void shouldSerializeAndDeserializeImageType() throws Exception {
    MediaTypeDto original = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.PNG)
            .width(800)
            .height(600)
            .build())
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"Image\"");
    assertThat(json).contains("\"format\":\"PNG\"");
    assertThat(json).contains("\"width\":800");
    assertThat(json).contains("\"height\":600");

    MediaTypeDto deserialized = objectMapper.readValue(json, MediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize video type")
  void shouldSerializeAndDeserializeVideoType() throws Exception {
    MediaTypeDto original = MediaTypeDto.builder()
        .video(VideoMediaTypeDto.builder()
            .format(MediaFormat.WEBM)
            .width(1920)
            .height(1080)
            .durationSeconds(300)
            .build())
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"Video\"");
    assertThat(json).contains("\"format\":\"WEBM\"");
    assertThat(json).contains("\"duration_seconds\":300");

    MediaTypeDto deserialized = objectMapper.readValue(json, MediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize audio type")
  void shouldSerializeAndDeserializeAudioType() throws Exception {
    MediaTypeDto original = MediaTypeDto.builder()
        .audio(AudioMediaTypeDto.builder()
            .format(MediaFormat.FLAC)
            .durationSeconds(180)
            .bitrate(320000)
            .build())
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"Audio\"");
    assertThat(json).contains("\"format\":\"FLAC\"");
    assertThat(json).contains("\"duration_seconds\":180");
    assertThat(json).contains("\"bitrate\":320000");

    MediaTypeDto deserialized = objectMapper.readValue(json, MediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty DTO")
  void shouldHandleEmptyDto() {
    MediaTypeDto dto = MediaTypeDto.builder().build();

    assertThat(dto.getImage()).isNull();
    assertThat(dto.getVideo()).isNull();
    assertThat(dto.getAudio()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    MediaTypeDto dto1 = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.JPEG)
            .width(1024)
            .height(768)
            .build())
        .build();

    MediaTypeDto dto2 = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.JPEG)
            .width(1024)
            .height(768)
            .build())
        .build();

    MediaTypeDto differentDto = MediaTypeDto.builder()
        .video(VideoMediaTypeDto.builder()
            .format(MediaFormat.MP4)
            .width(1280)
            .height(720)
            .build())
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
    MediaTypeDto dto = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.WEBP)
            .width(1920)
            .height(1080)
            .build())
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("MediaTypeDto");
    assertThat(toString).contains("image");
  }
}
