package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
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

import com.recipe_manager.model.mapper.RecipeCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;

/**
 * Component tests for DELETE /recipe-management/recipes/{recipeId} endpoint.
 * Tests recipe deletion with repository I/O mocked and real service logic.
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
class DeleteRecipeComponentTest extends AbstractComponentTest {

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
  @DisplayName("Should delete recipe for valid ID and owner")
  void shouldDeleteRecipe() throws Exception {
    // Setup repository mock
    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .userId(currentUserId)
        .title("Test Recipe")
        .description("A test recipe")
        .originUrl("http://example.com")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(15)
        .cookingTime(30)
        .difficulty(DifficultyLevel.MEDIUM)
        .build();

    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(123L)).thenReturn(Optional.of(recipe));
      doNothing().when(recipeRepository).delete(recipe);

      // When & Then
      mockMvc.perform(delete("/recipes/123")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/recipes/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldHandleInvalidRecipeIdFormat() throws Exception {
    // No repository setup needed as this should fail at parsing level

    mockMvc.perform(delete("/recipes/invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
