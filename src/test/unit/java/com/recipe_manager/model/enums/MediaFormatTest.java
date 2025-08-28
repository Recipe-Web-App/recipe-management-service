package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class MediaFormatTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have all expected image formats")
  void shouldHaveAllImageFormats() {
    assertThat(MediaFormat.JPEG).isNotNull();
    assertThat(MediaFormat.PNG).isNotNull();
    assertThat(MediaFormat.WEBP).isNotNull();
    assertThat(MediaFormat.AVIF).isNotNull();
    assertThat(MediaFormat.GIF).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have all expected video formats")
  void shouldHaveAllVideoFormats() {
    assertThat(MediaFormat.MP4).isNotNull();
    assertThat(MediaFormat.WEBM).isNotNull();
    assertThat(MediaFormat.MOV).isNotNull();
    assertThat(MediaFormat.AVI).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have all expected audio formats")
  void shouldHaveAllAudioFormats() {
    assertThat(MediaFormat.MP3).isNotNull();
    assertThat(MediaFormat.WAV).isNotNull();
    assertThat(MediaFormat.FLAC).isNotNull();
    assertThat(MediaFormat.OGG).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct total number of formats")
  void shouldHaveCorrectNumberOfFormats() {
    assertThat(MediaFormat.values()).hasSize(13);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return correct enum values")
  void shouldReturnCorrectEnumValues() {
    assertThat(MediaFormat.valueOf("JPEG")).isEqualTo(MediaFormat.JPEG);
    assertThat(MediaFormat.valueOf("MP4")).isEqualTo(MediaFormat.MP4);
    assertThat(MediaFormat.valueOf("MP3")).isEqualTo(MediaFormat.MP3);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should maintain ordinal order")
  void shouldMaintainOrdinalOrder() {
    MediaFormat[] formats = MediaFormat.values();

    // Image formats should come first
    assertThat(formats[0]).isEqualTo(MediaFormat.JPEG);
    assertThat(formats[1]).isEqualTo(MediaFormat.PNG);
    assertThat(formats[2]).isEqualTo(MediaFormat.WEBP);
    assertThat(formats[3]).isEqualTo(MediaFormat.AVIF);
    assertThat(formats[4]).isEqualTo(MediaFormat.GIF);

    // Video formats next
    assertThat(formats[5]).isEqualTo(MediaFormat.MP4);
    assertThat(formats[6]).isEqualTo(MediaFormat.WEBM);
    assertThat(formats[7]).isEqualTo(MediaFormat.MOV);
    assertThat(formats[8]).isEqualTo(MediaFormat.AVI);

    // Audio formats last
    assertThat(formats[9]).isEqualTo(MediaFormat.MP3);
    assertThat(formats[10]).isEqualTo(MediaFormat.WAV);
    assertThat(formats[11]).isEqualTo(MediaFormat.FLAC);
    assertThat(formats[12]).isEqualTo(MediaFormat.OGG);
  }
}
