package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/ingredients
 * endpoint.
 * Tests the actual IngredientService logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeIngredientMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class GetIngredientsComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealIngredientService(); // Use real service with mocked repositories
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients for valid recipe ID")
  void shouldReturnIngredientsForValidRecipeId() throws Exception {
    // Setup repository mock
    Ingredient ingredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();

    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .build();

    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .ingredient(ingredient)
        .recipe(recipe)
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    when(recipeIngredientRepository.findByRecipeRecipeId(123L))
        .thenReturn(Arrays.asList(recipeIngredient));

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.ingredients[0].ingredientName").value("Salt"))
        .andExpect(jsonPath("$.ingredients[0].quantity").value(1.5))
        .andExpect(jsonPath("$.ingredients[0].unit").value("TSP"))
        .andExpect(jsonPath("$.ingredients[0].isOptional").value(false))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return empty list for recipe with no ingredients")
  void shouldReturnEmptyListForRecipeWithNoIngredients() throws Exception {
    when(recipeIngredientRepository.findByRecipeRecipeId(456L))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/recipe-management/recipes/456/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.totalCount").value(0))
        .andExpect(jsonPath("$.ingredients").isEmpty())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldReturn400ForInvalidRecipeIdFormat() throws Exception {
    // No repository setup needed as this should fail at parsing level
    mockMvc.perform(get("/recipe-management/recipes/invalid/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
