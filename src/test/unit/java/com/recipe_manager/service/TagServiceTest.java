package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link TagService}.
 */
@Tag("standard-processing")
class TagServiceTest {

  private final TagService tagService = new TagService();

  @Test
  @DisplayName("addTag returns placeholder response")
  void addTag_returnsPlaceholder() {
    ResponseEntity<String> response = tagService.addTag("1");
    assertEquals("Add Tag - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("removeTag returns placeholder response")
  void removeTag_returnsPlaceholder() {
    ResponseEntity<String> response = tagService.removeTag("1");
    assertEquals("Remove Tag - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("getTags returns placeholder response")
  void getTags_returnsPlaceholder() {
    ResponseEntity<String> response = tagService.getTags("1");
    assertEquals("Get Tag - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
