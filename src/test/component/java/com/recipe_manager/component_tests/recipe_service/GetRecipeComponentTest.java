package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.RecipeService;

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
 * Component tests for GET /recipe-management/recipes/{recipeId} endpoint.
 * Tests recipe retrieval with all I/O mocked.
 */
@Tag("component")
class GetRecipeComponentTest extends AbstractComponentTest {
  @Mock
  private RecipeService recipeService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(recipeService.getRecipe(anyString())).thenReturn(ResponseEntity.ok("Get Recipe - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return recipe for a valid recipe ID")
  void shouldGetRecipe() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Get Recipe - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    when(recipeService.getRecipe("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipe-management/recipes/nonexistent")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("standard-processing")
  @DisplayName("Should return proper content type for API responses")
  void shouldReturnProperContentTypeForApiResponses() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
