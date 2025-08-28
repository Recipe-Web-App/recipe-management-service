package com.recipe_manager.model.dto.external.mediamanager.media;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.MediaFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class AudioMediaTypeDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
        .format(MediaFormat.MP3)
        .durationSeconds(240)
        .bitrate(128000)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.MP3);
    assertThat(dto.getDurationSeconds()).isEqualTo(240);
    assertThat(dto.getBitrate()).isEqualTo(128000);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    AudioMediaTypeDto original = AudioMediaTypeDto.builder()
        .format(MediaFormat.FLAC)
        .durationSeconds(180)
        .bitrate(320000)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"format\":\"FLAC\"");
    assertThat(json).contains("\"duration_seconds\":180");
    assertThat(json).contains("\"bitrate\":320000");

    AudioMediaTypeDto deserialized = objectMapper.readValue(json, AudioMediaTypeDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all audio formats")
  void shouldSupportAllAudioFormats() {
    MediaFormat[] audioFormats = {
        MediaFormat.MP3,
        MediaFormat.WAV,
        MediaFormat.FLAC,
        MediaFormat.OGG
    };

    for (MediaFormat format : audioFormats) {
      AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
          .format(format)
          .durationSeconds(120)
          .bitrate(128000)
          .build();

      assertThat(dto.getFormat()).isEqualTo(format);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various audio durations")
  void shouldHandleVariousAudioDurations() {
    int[] durations = {
        30,     // 30 seconds
        60,     // 1 minute
        180,    // 3 minutes
        240,    // 4 minutes
        300,    // 5 minutes
        600,    // 10 minutes
        1800,   // 30 minutes
        3600    // 1 hour
    };

    for (int duration : durations) {
      AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
          .format(MediaFormat.MP3)
          .durationSeconds(duration)
          .bitrate(128000)
          .build();

      assertThat(dto.getDurationSeconds()).isEqualTo(duration);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various audio bitrates")
  void shouldHandleVariousAudioBitrates() {
    int[] bitrates = {
        64000,    // 64 kbps
        96000,    // 96 kbps
        128000,   // 128 kbps
        160000,   // 160 kbps
        192000,   // 192 kbps
        256000,   // 256 kbps
        320000,   // 320 kbps
        1411200   // CD quality (1411.2 kbps)
    };

    for (int bitrate : bitrates) {
      AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
          .format(MediaFormat.MP3)
          .durationSeconds(120)
          .bitrate(bitrate)
          .build();

      assertThat(dto.getBitrate()).isEqualTo(bitrate);
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null duration")
  void shouldHandleNullDuration() {
    AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
        .format(MediaFormat.WAV)
        .durationSeconds(null)
        .bitrate(128000)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.WAV);
    assertThat(dto.getDurationSeconds()).isNull();
    assertThat(dto.getBitrate()).isEqualTo(128000);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null bitrate")
  void shouldHandleNullBitrate() {
    AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
        .format(MediaFormat.OGG)
        .durationSeconds(240)
        .bitrate(null)
        .build();

    assertThat(dto.getFormat()).isEqualTo(MediaFormat.OGG);
    assertThat(dto.getDurationSeconds()).isEqualTo(240);
    assertThat(dto.getBitrate()).isNull();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle all null values")
  void shouldHandleAllNullValues() {
    AudioMediaTypeDto dto = AudioMediaTypeDto.builder().build();

    assertThat(dto.getFormat()).isNull();
    assertThat(dto.getDurationSeconds()).isNull();
    assertThat(dto.getBitrate()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle lossless audio formats")
  void shouldHandleLosslessAudioFormats() {
    AudioMediaTypeDto flacDto = AudioMediaTypeDto.builder()
        .format(MediaFormat.FLAC)
        .durationSeconds(180)
        .bitrate(1411200)
        .build();

    AudioMediaTypeDto wavDto = AudioMediaTypeDto.builder()
        .format(MediaFormat.WAV)
        .durationSeconds(200)
        .bitrate(1411200)
        .build();

    assertThat(flacDto.getFormat()).isEqualTo(MediaFormat.FLAC);
    assertThat(wavDto.getFormat()).isEqualTo(MediaFormat.WAV);
    assertThat(flacDto.getBitrate()).isEqualTo(1411200);
    assertThat(wavDto.getBitrate()).isEqualTo(1411200);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle compressed audio formats")
  void shouldHandleCompressedAudioFormats() {
    AudioMediaTypeDto mp3Dto = AudioMediaTypeDto.builder()
        .format(MediaFormat.MP3)
        .durationSeconds(240)
        .bitrate(128000)
        .build();

    AudioMediaTypeDto oggDto = AudioMediaTypeDto.builder()
        .format(MediaFormat.OGG)
        .durationSeconds(180)
        .bitrate(192000)
        .build();

    assertThat(mp3Dto.getFormat()).isEqualTo(MediaFormat.MP3);
    assertThat(oggDto.getFormat()).isEqualTo(MediaFormat.OGG);
    assertThat(mp3Dto.getBitrate()).isEqualTo(128000);
    assertThat(oggDto.getBitrate()).isEqualTo(192000);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    AudioMediaTypeDto dto1 = AudioMediaTypeDto.builder()
        .format(MediaFormat.MP3)
        .durationSeconds(240)
        .bitrate(128000)
        .build();

    AudioMediaTypeDto dto2 = AudioMediaTypeDto.builder()
        .format(MediaFormat.MP3)
        .durationSeconds(240)
        .bitrate(128000)
        .build();

    AudioMediaTypeDto differentDto = AudioMediaTypeDto.builder()
        .format(MediaFormat.FLAC)
        .durationSeconds(180)
        .bitrate(320000)
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
    AudioMediaTypeDto dto = AudioMediaTypeDto.builder()
        .format(MediaFormat.FLAC)
        .durationSeconds(300)
        .bitrate(1411200)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("AudioMediaTypeDto");
    assertThat(toString).contains("FLAC");
    assertThat(toString).contains("300");
    assertThat(toString).contains("1411200");
  }
}
