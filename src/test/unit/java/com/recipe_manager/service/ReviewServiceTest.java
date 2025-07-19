package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link ReviewService}.
 */
@Tag("standard-processing")
class ReviewServiceTest {

  private final ReviewService reviewService = new ReviewService();

  @Test
  @DisplayName("getReviews returns placeholder response")
  void getReviews_returnsPlaceholder() {
    ResponseEntity<String> response = reviewService.getReviews("1");
    assertEquals("Get Recipe Reviews - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("addReview returns placeholder response")
  void addReview_returnsPlaceholder() {
    ResponseEntity<String> response = reviewService.addReview("1");
    assertEquals("Add Recipe Review - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("editReview returns placeholder response")
  void editReview_returnsPlaceholder() {
    ResponseEntity<String> response = reviewService.editReview("1");
    assertEquals("Edit Recipe Review - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("deleteReview returns placeholder response")
  void deleteReview_returnsPlaceholder() {
    ResponseEntity<String> response = reviewService.deleteReview("1");
    assertEquals("Delete Recipe Review - placeholder", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
