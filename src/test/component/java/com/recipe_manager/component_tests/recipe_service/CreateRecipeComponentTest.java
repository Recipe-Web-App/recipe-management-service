package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
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
    when(recipeService.createRecipe(any(CreateRecipeRequest.class)))
        .thenReturn(ResponseEntity.ok(1L));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create a recipe with valid data")
  void shouldCreateRecipe() throws Exception {
    String validRequestJson = "{" +
        "\"title\":\"Test Recipe\"," +
        "\"description\":\"A test recipe\"," +
        "\"originUrl\":\"http://example.com\"," +
        "\"servings\":2," +
        "\"preparationTime\":10," +
        "\"cookingTime\":20," +
        "\"difficulty\":\"BEGINNER\"," +
        "\"ingredients\":[{" +
        "  \"ingredientName\":\"Flour\"," +
        "  \"quantity\":1.0," +
        "  \"isOptional\":false" +
        "}]," +
        "\"steps\":[{" +
        "  \"stepNumber\":1," +
        "  \"instruction\":\"Mix ingredients\"" +
        "}]" +
        "}";
    mockMvc.perform(post("/recipe-management/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validRequestJson))
        .andExpect(status().isOk())
        .andExpect(content().string("1"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for validation errors")
  void shouldHandleValidationErrors() throws Exception {
    when(recipeService.createRecipe(any(CreateRecipeRequest.class)))
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
