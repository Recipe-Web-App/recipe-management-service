package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.mapper.RecipeCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for PUT /recipe-management/recipes/{recipeId} endpoint.
 * Tests the actual RecipeService logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeMapperImpl.class,
    RecipeIngredientMapperImpl.class,
    RecipeStepMapperImpl.class,
    RecipeFavoriteMapperImpl.class,
    RecipeRevisionMapperImpl.class,
    RecipeTagMapperImpl.class,
    RecipeCommentMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class UpdateRecipeComponentTest extends AbstractComponentTest {

  private UUID currentUserId;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealRecipeService(); // Use real service with mocked repositories
    currentUserId = UUID.randomUUID();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update a recipe with valid data")
  void shouldUpdateRecipe() throws Exception {
    // Setup existing recipe
    Recipe existingRecipe = Recipe.builder()
        .recipeId(123L)
        .userId(currentUserId)
        .title("Original Recipe")
        .description("Original description")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(15)
        .cookingTime(30)
        .difficulty(DifficultyLevel.MEDIUM)
        .build();

    Recipe updatedRecipe = Recipe.builder()
        .recipeId(123L)
        .userId(currentUserId)
        .title("Updated Recipe")
        .description("Updated description")
        .servings(BigDecimal.valueOf(6))
        .preparationTime(20)
        .cookingTime(40)
        .difficulty(DifficultyLevel.EXPERT)
        .build();

    Ingredient existingIngredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();

    when(recipeRepository.findById(123L)).thenReturn(Optional.of(existingRecipe));
    when(ingredientRepository.findByNameIgnoreCase("Salt")).thenReturn(Optional.of(existingIngredient));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);

    String updateRequestJson = "{\"title\":\"Updated Recipe\",\"description\":\"Updated description\",\"servings\":6,\"preparationTime\":20,\"cookingTime\":40,\"difficulty\":\"EXPERT\",\"ingredients\":[{\"ingredientName\":\"Salt\",\"quantity\":0.5,\"unit\":\"TSP\",\"isOptional\":false}],\"steps\":[]}";

    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(put("/recipes/123")
          .contentType(MediaType.APPLICATION_JSON)
          .content(updateRequestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.title").value("Updated Recipe"))
          .andExpect(jsonPath("$.description").value("Updated description"))
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 403 forbidden when user tries to update recipe they don't own")
  void shouldHandleForbiddenForUnauthorizedUser() throws Exception {
    UUID recipeOwnerUserId = UUID.randomUUID();
    UUID differentUserId = UUID.randomUUID();

    // Setup existing recipe owned by a different user
    Recipe existingRecipe = Recipe.builder()
        .recipeId(456L)
        .userId(recipeOwnerUserId) // Recipe owned by different user
        .title("Someone else's recipe")
        .description("A recipe owned by another user")
        .servings(BigDecimal.valueOf(2))
        .preparationTime(10)
        .cookingTime(15)
        .difficulty(DifficultyLevel.EASY)
        .build();

    when(recipeRepository.findById(456L)).thenReturn(Optional.of(existingRecipe));

    String updateRequestJson = "{\"title\":\"Trying to update someone else's recipe\"}";

    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(differentUserId);

      mockMvc.perform(put("/recipes/456")
          .contentType(MediaType.APPLICATION_JSON)
          .content(updateRequestJson))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when updating recipe")
  void shouldHandleNotFoundForNonExistentRecipeUpdate() throws Exception {
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    String updateRequestJson = "{\"title\":\"Updated Recipe\"}";

    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(put("/recipes/999")
          .contentType(MediaType.APPLICATION_JSON)
          .content(updateRequestJson))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }
}
