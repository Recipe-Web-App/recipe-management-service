package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.ingredient.IngredientComment;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for POST
 * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment
 * endpoint.
 * Tests the actual IngredientService logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.RecipeIngredientMapperImpl.class,
    com.recipe_manager.model.mapper.IngredientCommentMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class AddIngredientCommentComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealIngredientService(); // Use real service with mocked repositories
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add comment to ingredient successfully")
  @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000")
  void shouldAddCommentToIngredientSuccessfully() throws Exception {
    // Setup repository mock
    Ingredient ingredient = Ingredient.builder()
        .ingredientId(456L)
        .name("Salt")
        .build();

    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .build();

    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .ingredient(ingredient)
        .recipe(recipe)
        .build();

    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(123L, 456L))
        .thenReturn(Optional.of(recipeIngredient));
    when(ingredientCommentRepository.save(any(IngredientComment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(456L))
        .thenReturn(List.of()); // Return empty list for this test

    // When & Then
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"This is a test comment\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.ingredientId").value(456))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for empty comment")
  void shouldReturn400ForEmptyComment() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldReturn400ForInvalidRecipeIdFormat() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/invalid/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"Test comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid ingredient ID format")
  void shouldReturn400ForInvalidIngredientIdFormat() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/ingredients/invalid/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"Test comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for non-existent recipe ingredient")
  void shouldReturn400ForNonExistentRecipeIngredient() throws Exception {
    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(999L, 888L))
        .thenReturn(Optional.empty());

    mockMvc.perform(post("/recipe-management/recipes/999/ingredients/888/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"Test comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
