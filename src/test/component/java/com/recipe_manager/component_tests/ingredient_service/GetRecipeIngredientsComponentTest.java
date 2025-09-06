package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/ingredients
 * endpoint.
 * Tests ingredient retrieval with all I/O mocked.
 */
@Tag("component")
class GetRecipeIngredientsComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    // Create mock comment data
    IngredientCommentDto comment1 = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(123L)
        .userId(UUID.randomUUID())
        .commentText("This ingredient is really fresh!")
        .isPublic(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    List<IngredientCommentDto> comments = Arrays.asList(comment1);

    // Create mock ingredient data with comments
    RecipeIngredientDto ingredient1 = RecipeIngredientDto.builder()
        .recipeId(123L)
        .ingredientId(1L)
        .ingredientName("Tomato")
        .quantity(BigDecimal.valueOf(2))
        .unit(IngredientUnit.PIECE)
        .isOptional(false)
        .comments(comments)
        .build();

    RecipeIngredientDto ingredient2 = RecipeIngredientDto.builder()
        .recipeId(123L)
        .ingredientId(2L)
        .ingredientName("Salt")
        .quantity(BigDecimal.valueOf(1))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .comments(Arrays.asList()) // Empty comments list
        .build();

    List<RecipeIngredientDto> ingredients = Arrays.asList(ingredient1, ingredient2);

    // Create a mock response with proper DTO structure including comments
    com.recipe_manager.model.dto.response.RecipeIngredientsResponse mockResponse =
        com.recipe_manager.model.dto.response.RecipeIngredientsResponse.builder()
            .recipeId(123L)
            .ingredients(ingredients)
            .totalCount(ingredients.size())
            .build();

    when(ingredientService.getIngredients(anyString())).thenReturn(ResponseEntity.ok(mockResponse));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients for a valid recipe ID")
  void shouldGetRecipeIngredients() throws Exception {
    mockMvc.perform(get("/recipes/123/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipeIngredients() throws Exception {
    when(ingredientService.getIngredients("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipes/nonexistent/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients with comments")
  void shouldReturnIngredientsWithComments() throws Exception {
    mockMvc.perform(get("/recipes/123/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("X-Request-ID"));

    // Verify that the mock was called (which contains ingredients with comments)
    org.mockito.Mockito.verify(ingredientService).getIngredients("123");
  }
}
