package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for PUT
 * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment
 * endpoint.
 * Tests the actual IngredientService logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.RecipeIngredientMapperImpl.class,
    com.recipe_manager.model.mapper.IngredientCommentMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeRevisionMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class EditIngredientCommentComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealIngredientService(); // Use real service with mocked repositories
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit comment on ingredient successfully")
  void shouldEditCommentOnIngredientSuccessfully() throws Exception {
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
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
    when(ingredientCommentRepository.findByCommentIdAndIngredientIngredientId(1L, 456L))
        .thenReturn(Optional.of(createMockIngredientComment(1L, "Original comment")));
    when(ingredientCommentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(456L))
        .thenReturn(java.util.Collections.emptyList());

    // When & Then
    mockMvc.perform(put("/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.ingredientId").value(456))
        .andExpect(jsonPath("$.comments.length()").value(0))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid comment index")
  void shouldReturn400ForInvalidCommentIndex() throws Exception {
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

    mockMvc.perform(put("/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":5,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for negative comment index")
  void shouldReturn400ForNegativeCommentIndex() throws Exception {
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

    mockMvc.perform(put("/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":-1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for empty comment")
  void shouldReturn400ForEmptyComment() throws Exception {
    mockMvc.perform(put("/recipes/123/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldReturn400ForInvalidRecipeIdFormat() throws Exception {
    mockMvc.perform(put("/recipes/invalid/ingredients/456/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid ingredient ID format")
  void shouldReturn400ForInvalidIngredientIdFormat() throws Exception {
    mockMvc.perform(put("/recipes/123/ingredients/invalid/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for non-existent recipe ingredient")
  void shouldReturn400ForNonExistentRecipeIngredient() throws Exception {
    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(999L, 888L))
        .thenReturn(Optional.empty());

    mockMvc.perform(put("/recipes/999/ingredients/888/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  private IngredientComment createMockIngredientComment(Long commentId, String commentText) {
    return IngredientComment.builder()
        .commentId(commentId)
        .recipeId(123L)
        .commentText(commentText)
        .build();
  }
}
