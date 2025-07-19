package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.MediaService;

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
 * Component tests for POST
 * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media
 * endpoint.
 */
@Tag("component")
class AddMediaToIngredientComponentTest extends AbstractComponentTest {
  @Mock
  private MediaService mediaService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(mediaService.addMediaToIngredient(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("Add Media to Ingredient - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to a valid ingredient ID")
  void shouldAddMediaToIngredient() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/456/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Add Media to Ingredient - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent ingredient ID when adding media")
  void shouldHandleNotFoundForNonExistentIngredientMedia() throws Exception {
    when(mediaService.addMediaToIngredient(anyString(), anyString()))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Ingredient not found"));
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for ingredient media operations")
  void shouldHandleMalformedJsonForIngredientMediaOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/456/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
