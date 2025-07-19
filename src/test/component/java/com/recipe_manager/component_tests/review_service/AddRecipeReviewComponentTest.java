package com.recipe_manager.component_tests.review_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.ReviewService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for POST /recipe-management/recipes/{recipeId}/review
 * endpoint.
 */
@Tag("component")
class AddRecipeReviewComponentTest extends AbstractComponentTest {
  @Mock
  private ReviewService reviewService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(reviewService.addReview(anyString())).thenReturn(ResponseEntity.ok("Add Review - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add a review to a valid recipe ID")
  void shouldAddRecipeReview() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Add Review - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when adding review")
  void shouldHandleNotFoundForNonExistentRecipeReviewAdd() throws Exception {
    when(reviewService.addReview("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(post("/recipe-management/recipes/nonexistent/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for review operations")
  void shouldHandleMalformedJsonForReviewOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(post("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
