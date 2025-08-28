package com.recipe_manager.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for MediaType enum.
 */
@Tag("unit")
class MediaTypeTest {

  @Test
  void testGetMimeType_ReturnsCorrectValues() {
    assertEquals("image/jpeg", MediaType.IMAGE_JPEG.getMimeType());
    assertEquals("image/png", MediaType.IMAGE_PNG.getMimeType());
    assertEquals("image/gif", MediaType.IMAGE_GIF.getMimeType());
    assertEquals("image/webp", MediaType.IMAGE_WEBP.getMimeType());
    assertEquals("image/avif", MediaType.IMAGE_AVIF.getMimeType());
    assertEquals("image/svg+xml", MediaType.IMAGE_SVG_XML.getMimeType());
    assertEquals("image/heic", MediaType.IMAGE_HEIC.getMimeType());
    assertEquals("image/tiff", MediaType.IMAGE_TIFF.getMimeType());
    assertEquals("video/mp4", MediaType.VIDEO_MP4.getMimeType());
    assertEquals("video/webm", MediaType.VIDEO_WEBM.getMimeType());
    assertEquals("video/ogg", MediaType.VIDEO_OGG.getMimeType());
    assertEquals("video/quicktime", MediaType.VIDEO_QUICKTIME.getMimeType());
  }

  @Test
  void testFromMimeType_ValidMimeTypes_ReturnsCorrectEnum() {
    assertEquals(MediaType.IMAGE_JPEG, MediaType.fromMimeType("image/jpeg"));
    assertEquals(MediaType.IMAGE_PNG, MediaType.fromMimeType("image/png"));
    assertEquals(MediaType.VIDEO_MP4, MediaType.fromMimeType("video/mp4"));
    assertEquals(MediaType.VIDEO_WEBM, MediaType.fromMimeType("video/webm"));
  }

  @Test
  void testFromMimeType_InvalidMimeType_ReturnsNull() {
    assertNull(MediaType.fromMimeType("invalid/type"));
    assertNull(MediaType.fromMimeType("text/plain"));
    assertNull(MediaType.fromMimeType("application/json"));
  }

  @Test
  void testFromMimeType_NullMimeType_ReturnsNull() {
    assertNull(MediaType.fromMimeType(null));
  }

  @Test
  void testFromMimeType_EmptyMimeType_ReturnsNull() {
    assertNull(MediaType.fromMimeType(""));
  }

  @Test
  void testFromMimeType_CaseSensitive() {
    assertNull(MediaType.fromMimeType("IMAGE/JPEG"));
    assertNull(MediaType.fromMimeType("Image/Jpeg"));
  }

  @Test
  void testToString_ReturnsCorrectMimeType() {
    assertEquals("image/jpeg", MediaType.IMAGE_JPEG.toString());
    assertEquals("image/png", MediaType.IMAGE_PNG.toString());
    assertEquals("video/mp4", MediaType.VIDEO_MP4.toString());
    assertEquals("video/webm", MediaType.VIDEO_WEBM.toString());
  }

  @Test
  void testAllImageTypes_ArePresent() {
    assertEquals(MediaType.IMAGE_JPEG, MediaType.fromMimeType("image/jpeg"));
    assertEquals(MediaType.IMAGE_PNG, MediaType.fromMimeType("image/png"));
    assertEquals(MediaType.IMAGE_GIF, MediaType.fromMimeType("image/gif"));
    assertEquals(MediaType.IMAGE_WEBP, MediaType.fromMimeType("image/webp"));
    assertEquals(MediaType.IMAGE_AVIF, MediaType.fromMimeType("image/avif"));
    assertEquals(MediaType.IMAGE_SVG_XML, MediaType.fromMimeType("image/svg+xml"));
    assertEquals(MediaType.IMAGE_HEIC, MediaType.fromMimeType("image/heic"));
    assertEquals(MediaType.IMAGE_TIFF, MediaType.fromMimeType("image/tiff"));
  }

  @Test
  void testAllVideoTypes_ArePresent() {
    assertEquals(MediaType.VIDEO_MP4, MediaType.fromMimeType("video/mp4"));
    assertEquals(MediaType.VIDEO_WEBM, MediaType.fromMimeType("video/webm"));
    assertEquals(MediaType.VIDEO_OGG, MediaType.fromMimeType("video/ogg"));
    assertEquals(MediaType.VIDEO_QUICKTIME, MediaType.fromMimeType("video/quicktime"));
  }
}
