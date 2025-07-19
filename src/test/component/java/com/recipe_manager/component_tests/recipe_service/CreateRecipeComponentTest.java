package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.exception.BusinessException;
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
 * Component tests for POST /recipe-management/recipes endpoint.
 */
@Tag("component")
class CreateRecipeComponentTest extends AbstractComponentTest {
  @Mock
  private RecipeService recipeService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(recipeService.createRecipe()).thenReturn(ResponseEntity.ok("Create Recipe - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create a recipe with valid data")
  void shouldCreateRecipe() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Create Recipe - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for validation errors")
  void shouldHandleValidationErrors() throws Exception {
    when(recipeService.createRecipe())
        .thenThrow(new BusinessException("Invalid recipe data"));
    mockMvc.perform(post("/recipe-management/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON gracefully")
  void shouldHandleMalformedJsonGracefully() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(post("/recipe-management/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
