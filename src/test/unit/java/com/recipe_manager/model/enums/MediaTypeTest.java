package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for MediaType enum.
 */
@Tag("unit")
class MediaTypeTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    MediaType[] values = MediaType.values();

    // Then
    assertThat(values).containsExactlyInAnyOrder(
        // Image formats
        MediaType.IMAGE_JPEG,
        MediaType.IMAGE_PNG,
        MediaType.IMAGE_GIF,
        MediaType.IMAGE_WEBP,
        MediaType.IMAGE_AVIF,
        MediaType.IMAGE_SVG_XML,
        MediaType.IMAGE_HEIC,
        MediaType.IMAGE_TIFF,
        // Video formats
        MediaType.VIDEO_MP4,
        MediaType.VIDEO_WEBM,
        MediaType.VIDEO_OGG,
        MediaType.VIDEO_QUICKTIME);
  }

  @Test
  @DisplayName("Should have correct MIME type values")
  @Tag("standard-processing")
  void shouldHaveCorrectMimeTypeValues() {
    // Then - Image formats
    assertThat(MediaType.IMAGE_JPEG.getMimeType()).isEqualTo("image/jpeg");
    assertThat(MediaType.IMAGE_PNG.getMimeType()).isEqualTo("image/png");
    assertThat(MediaType.IMAGE_GIF.getMimeType()).isEqualTo("image/gif");
    assertThat(MediaType.IMAGE_WEBP.getMimeType()).isEqualTo("image/webp");
    assertThat(MediaType.IMAGE_AVIF.getMimeType()).isEqualTo("image/avif");
    assertThat(MediaType.IMAGE_SVG_XML.getMimeType()).isEqualTo("image/svg+xml");
    assertThat(MediaType.IMAGE_HEIC.getMimeType()).isEqualTo("image/heic");
    assertThat(MediaType.IMAGE_TIFF.getMimeType()).isEqualTo("image/tiff");

    // Then - Video formats
    assertThat(MediaType.VIDEO_MP4.getMimeType()).isEqualTo("video/mp4");
    assertThat(MediaType.VIDEO_WEBM.getMimeType()).isEqualTo("video/webm");
    assertThat(MediaType.VIDEO_OGG.getMimeType()).isEqualTo("video/ogg");
    assertThat(MediaType.VIDEO_QUICKTIME.getMimeType()).isEqualTo("video/quicktime");
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String jpegString = "IMAGE_JPEG";
    String mp4String = "VIDEO_MP4";
    String webpString = "IMAGE_WEBP";

    // When & Then
    assertThat(MediaType.valueOf(jpegString)).isEqualTo(MediaType.IMAGE_JPEG);
    assertThat(MediaType.valueOf(mp4String)).isEqualTo(MediaType.VIDEO_MP4);
    assertThat(MediaType.valueOf(webpString)).isEqualTo(MediaType.IMAGE_WEBP);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(MediaType.IMAGE_JPEG.name()).isEqualTo("IMAGE_JPEG");
    assertThat(MediaType.VIDEO_MP4.name()).isEqualTo("VIDEO_MP4");
    assertThat(MediaType.IMAGE_PNG.name()).isEqualTo("IMAGE_PNG");
    assertThat(MediaType.VIDEO_WEBM.name()).isEqualTo("VIDEO_WEBM");
  }

  @Test
  @DisplayName("Should return MIME type as toString")
  @Tag("standard-processing")
  void shouldReturnMimeTypeAsToString() {
    // Then
    assertThat(MediaType.IMAGE_JPEG).hasToString("image/jpeg");
    assertThat(MediaType.VIDEO_MP4).hasToString("video/mp4");
    assertThat(MediaType.IMAGE_SVG_XML).hasToString("image/svg+xml");
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then - Image formats should come first
    assertThat(MediaType.IMAGE_JPEG.ordinal()).isZero();
    assertThat(MediaType.IMAGE_PNG.ordinal()).isEqualTo(1);
    assertThat(MediaType.IMAGE_GIF.ordinal()).isEqualTo(2);

    // Video formats should come after images
    assertThat(MediaType.VIDEO_MP4.ordinal()).isEqualTo(8);
    assertThat(MediaType.VIDEO_WEBM.ordinal()).isEqualTo(9);
  }

  @Test
  @DisplayName("Should be distinct types")
  @Tag("standard-processing")
  void shouldBeDistinctTypes() {
    // Then
    assertThat(MediaType.IMAGE_JPEG)
        .isNotEqualTo(MediaType.IMAGE_PNG)
        .isNotEqualTo(MediaType.VIDEO_MP4);
    assertThat(MediaType.VIDEO_MP4)
        .isNotEqualTo(MediaType.VIDEO_WEBM);
  }
}
