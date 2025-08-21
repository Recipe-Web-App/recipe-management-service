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

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.IngredientCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET
 * /recipe-management/recipes/{recipeId}/ingredients/scale endpoint.
 * Tests the actual IngredientService scaling logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeIngredientMapperImpl.class,
    IngredientCommentMapperImpl.class,
    RecipeRevisionMapperImpl.class,
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class ScaleRecipeIngredientsComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealIngredientService(); // Use real service with mocked repositories
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale ingredients for a valid recipe ID and quantity")
  void shouldScaleRecipeIngredients() throws Exception {
    // Setup repository mock
    Ingredient ingredient1 = Ingredient.builder()
        .ingredientId(1L)
        .name("Flour")
        .build();

    Ingredient ingredient2 = Ingredient.builder()
        .ingredientId(2L)
        .name("Sugar")
        .build();

    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .build();

    RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
        .ingredient(ingredient1)
        .recipe(recipe)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    RecipeIngredient recipeIngredient2 = RecipeIngredient.builder()
        .ingredient(ingredient2)
        .recipe(recipe)
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.CUP)
        .isOptional(true)
        .build();

    when(recipeIngredientRepository.findByRecipeRecipeId(123L))
        .thenReturn(Arrays.asList(recipeIngredient1, recipeIngredient2));

    // Mock comment repository for both ingredients
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(1L))
        .thenReturn(Collections.emptyList());
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(2L))
        .thenReturn(Collections.emptyList());

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .param("quantity", "2.5")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.ingredients").isArray())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.ingredients[0].ingredientName").value("Flour"))
        .andExpect(jsonPath("$.ingredients[0].quantity").value(2.5)) // 1.0 * 2.5 = 2.5
        .andExpect(jsonPath("$.ingredients[0].isOptional").value(false))
        .andExpect(jsonPath("$.ingredients[0].comments").isArray())
        .andExpect(jsonPath("$.ingredients[0].comments").isEmpty())
        .andExpect(jsonPath("$.ingredients[1].ingredientName").value("Sugar"))
        .andExpect(jsonPath("$.ingredients[1].quantity").value(1.25)) // 0.5 * 2.5 = 1.25
        .andExpect(jsonPath("$.ingredients[1].isOptional").value(true))
        .andExpect(jsonPath("$.ingredients[1].comments").isArray())
        .andExpect(jsonPath("$.ingredients[1].comments").isEmpty())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale empty ingredients list")
  void shouldScaleEmptyIngredientsList() throws Exception {
    when(recipeIngredientRepository.findByRecipeRecipeId(456L))
        .thenReturn(Collections.emptyList());

    // No comment mocking needed since there are no ingredients

    mockMvc.perform(get("/recipe-management/recipes/456/ingredients/scale")
        .param("quantity", "3.0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.ingredients").isArray())
        .andExpect(jsonPath("$.totalCount").value(0))
        .andExpect(jsonPath("$.ingredients").isEmpty())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid quantity parameter")
  void shouldHandleInvalidQuantityParameter() throws Exception {
    // No repository setup needed as this should fail at parameter parsing level
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .param("quantity", "invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for missing quantity parameter")
  void shouldHandleMissingQuantityParameter() throws Exception {
    // No repository setup needed as this should fail at parameter validation level
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldReturn400ForInvalidRecipeIdFormat() throws Exception {
    // No repository setup needed as this should fail at parsing level
    mockMvc.perform(get("/recipe-management/recipes/invalid/ingredients/scale")
        .param("quantity", "2.0")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
