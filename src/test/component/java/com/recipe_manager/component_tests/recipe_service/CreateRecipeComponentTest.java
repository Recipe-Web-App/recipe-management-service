package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Component tests for POST /recipe-management/recipes endpoint.
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
class CreateRecipeComponentTest extends AbstractComponentTest {

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
  @DisplayName("Should create a recipe with valid data")
  void shouldCreateRecipe() throws Exception {
    // Setup repository mocks
    Ingredient flourIngredient = Ingredient.builder()
        .name("Flour")
        .build();

    Recipe savedRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Test Recipe")
        .description("A test recipe")
        .originUrl("http://example.com")
        .servings(BigDecimal.valueOf(2))
        .preparationTime(10)
        .cookingTime(20)
        .difficulty(DifficultyLevel.BEGINNER)
        .build();

    when(ingredientRepository.findByNameIgnoreCase("Flour")).thenReturn(Optional.of(flourIngredient));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

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
        "  \"unit\":\"G\"," +
        "  \"isOptional\":false" +
        "}]," +
        "\"steps\":[{" +
        "  \"stepNumber\":1," +
        "  \"instruction\":\"Mix ingredients\"," +
        "  \"optional\":false" +
        "}]" +
        "}";

    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(post("/recipe-management/recipes")
          .contentType(MediaType.APPLICATION_JSON)
          .content(validRequestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(1))
          .andExpect(jsonPath("$.title").value("Test Recipe"))
          .andExpect(jsonPath("$.description").value("A test recipe"))
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create recipe and new ingredient when ingredient doesn't exist")
  void shouldCreateRecipeWithNewIngredient() throws Exception {
    // Setup repository mocks - ingredient doesn't exist, so service will create it
    Ingredient newIngredient = Ingredient.builder()
        .name("New Spice")
        .build();

    Recipe savedRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Spicy Recipe")
        .description("A spicy test recipe")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(5)
        .cookingTime(15)
        .difficulty(DifficultyLevel.EASY)
        .build();

    when(ingredientRepository.findByNameIgnoreCase("New Spice")).thenReturn(Optional.empty());
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    String validRequestJson = "{" +
        "\"title\":\"Spicy Recipe\"," +
        "\"description\":\"A spicy test recipe\"," +
        "\"servings\":4," +
        "\"preparationTime\":5," +
        "\"cookingTime\":15," +
        "\"difficulty\":\"EASY\"," +
        "\"ingredients\":[{" +
        "  \"ingredientName\":\"New Spice\"," +
        "  \"quantity\":5.0," +
        "  \"unit\":\"G\"," +
        "  \"isOptional\":false" +
        "}]," +
        "\"steps\":[{" +
        "  \"stepNumber\":1," +
        "  \"instruction\":\"Add spice and cook\"," +
        "  \"optional\":false" +
        "}]" +
        "}";

    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(post("/recipe-management/recipes")
          .contentType(MediaType.APPLICATION_JSON)
          .content(validRequestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(1))
          .andExpect(jsonPath("$.title").value("Spicy Recipe"))
          .andExpect(jsonPath("$.description").value("A spicy test recipe"))
          .andExpect(header().exists("X-Request-ID"));
    }
  }
}
