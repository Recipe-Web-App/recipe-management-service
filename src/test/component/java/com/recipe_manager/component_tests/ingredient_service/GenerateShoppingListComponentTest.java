package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.ShoppingListMapperImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET
 * /recipe-management/recipes/{recipeId}/ingredients/shopping-list endpoint.
 * Tests the actual IngredientService logic with mocked repository calls.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {
    RecipeIngredientMapperImpl.class,
    ShoppingListMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeRevisionMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class GenerateShoppingListComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    // Mock RecipeScraperService to return empty pricing data for existing tests
    RecipeScraperShoppingDto emptyPricingData = RecipeScraperShoppingDto.builder()
        .recipeId(0L)
        .ingredients(java.util.Collections.emptyMap())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();
    lenient().when(recipeScraperService.getShoppingInfo(any(Long.class)))
        .thenReturn(CompletableFuture.completedFuture(emptyPricingData));

    useRealIngredientService(); // Use real service with mocked repositories
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate shopping list for a valid recipe ID")
  void shouldGenerateShoppingList() throws Exception {
    // Setup repository mock data
    Recipe recipe = Recipe.builder().recipeId(123L).build();

    Ingredient salt = Ingredient.builder().name("Salt").build();
    Ingredient pepper = Ingredient.builder().name("Pepper").build();

    RecipeIngredient ingredient1 = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(salt)
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    RecipeIngredient ingredient2 = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(pepper)
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);
    when(recipeIngredientRepository.findByRecipeRecipeId(123L)).thenReturn(ingredients);

    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.items[0].ingredientName").value("Salt"))
        .andExpect(jsonPath("$.items[0].totalQuantity").value(1.5))
        .andExpect(jsonPath("$.items[1].ingredientName").value("Pepper"))
        .andExpect(jsonPath("$.items[1].totalQuantity").value(0.5))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid recipe ID format")
  void shouldHandleInvalidRecipeIdFormat() throws Exception {
    // No repository setup needed as this should fail at parsing level
    mockMvc.perform(get("/recipe-management/recipes/invalid/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return empty shopping list for recipe with no ingredients")
  void shouldReturnEmptyShoppingListForRecipeWithNoIngredients() throws Exception {
    // Setup repository to return empty list
    when(recipeIngredientRepository.findByRecipeRecipeId(456L)).thenReturn(Arrays.asList());

    mockMvc.perform(get("/recipe-management/recipes/456/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.totalCount").value(0))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items").isEmpty())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should aggregate duplicate ingredients in shopping list")
  void shouldAggregateDuplicateIngredientsInShoppingList() throws Exception {
    // Setup repository mock data with duplicate salt ingredients
    Recipe recipe = Recipe.builder().recipeId(789L).build();
    Ingredient salt = Ingredient.builder().name("Salt").build();

    RecipeIngredient ingredient1 = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(salt)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    RecipeIngredient ingredient2 = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(salt)
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);
    when(recipeIngredientRepository.findByRecipeRecipeId(789L)).thenReturn(ingredients);

    mockMvc.perform(get("/recipe-management/recipes/789/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(789))
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.items[0].ingredientName").value("Salt"))
        .andExpect(jsonPath("$.items[0].totalQuantity").value(1.5))
        .andExpect(header().exists("X-Request-ID"));
  }
}
