package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId} endpoint.
 * Tests the actual RecipeService logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeMapperImpl.class,
    RecipeIngredientMapperImpl.class,
    RecipeStepMapperImpl.class,
    RecipeFavoriteMapperImpl.class,
    RecipeRevisionMapperImpl.class,
    RecipeTagMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class GetRecipeComponentTest extends AbstractComponentTest {

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
  @DisplayName("Should return recipe for a valid recipe ID")
  void shouldGetRecipe() throws Exception {
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

    when(recipeRepository.findById(123L)).thenReturn(Optional.of(recipe));

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.title").value("Test Recipe"))
        .andExpect(jsonPath("$.description").value("A test recipe"))
        .andExpect(jsonPath("$.originUrl").value("http://example.com"))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.preparationTime").value(15))
        .andExpect(jsonPath("$.cookingTime").value(30))
        .andExpect(jsonPath("$.difficulty").value("MEDIUM"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/recipe-management/recipes/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldHandleInvalidRecipeIdFormat() throws Exception {
    // No repository setup needed as this should fail at parsing level

    mockMvc.perform(get("/recipe-management/recipes/invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
