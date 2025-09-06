package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientMatchMode;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for POST /recipe-management/recipes/search endpoint.
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
class SearchRecipesComponentTest extends AbstractComponentTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  private Recipe testRecipe1;
  private Recipe testRecipe2;
  private SearchRecipesRequest searchRequest;

  @BeforeEach
  void setUpTestData() {
    super.setUp();
    useRealRecipeService(); // Use real service with mocked repositories

    testRecipe1 = Recipe.builder()
        .recipeId(1L)
        .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        .title("Chicken Pasta")
        .description("Delicious pasta dish")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(30)
        .cookingTime(20)
        .difficulty(DifficultyLevel.EASY)
        .build();

    testRecipe2 = Recipe.builder()
        .recipeId(2L)
        .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        .title("Beef Stir Fry")
        .description("Quick and easy")
        .servings(BigDecimal.valueOf(2))
        .preparationTime(15)
        .cookingTime(10)
        .difficulty(DifficultyLevel.MEDIUM)
        .build();

    searchRequest = new SearchRecipesRequest();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should search recipes successfully with title query")
  void shouldSearchRecipesSuccessfullyWithTitleQuery() throws Exception {
    // Given
    searchRequest.setRecipeNameQuery("Chicken");

    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(testRecipe1),
        PageRequest.of(0, 20), 1);

    when(recipeRepository.searchRecipes(
        eq("Chicken"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(new String[0]),
        eq(new String[0]),
        any(Pageable.class)))
        .thenReturn(recipePage);

    // When & Then
    mockMvc.perform(post("/recipes/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/json"))
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(1))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
        .andExpect(jsonPath("$.recipes[0].title").value("Chicken Pasta"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(1))
        .andExpect(jsonPath("$.empty").value(false));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should search recipes with all criteria")
  void shouldSearchRecipesWithAllCriteria() throws Exception {
    // Given
    searchRequest.setRecipeNameQuery("pasta");
    searchRequest.setIngredients(Arrays.asList("chicken", "pasta"));
    searchRequest.setIngredientMatchMode(IngredientMatchMode.AND);
    searchRequest.setDifficulty(DifficultyLevel.EASY);
    searchRequest.setMaxCookingTime(30);
    searchRequest.setMaxPreparationTime(45);
    searchRequest.setMinServings(BigDecimal.valueOf(2));
    searchRequest.setMaxServings(BigDecimal.valueOf(6));

    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(testRecipe1),
        org.springframework.data.domain.PageRequest.of(0, 20), 1);

    when(recipeRepository.searchRecipes(
        eq("pasta"),
        eq("EASY"),
        eq(30),
        eq(45),
        eq(BigDecimal.valueOf(2)),
        eq(BigDecimal.valueOf(6)),
        eq(new String[]{"chicken", "pasta"}),
        eq(new String[0]),
        any(Pageable.class)))
        .thenReturn(recipePage);

    // When & Then
    mockMvc.perform(post("/recipes/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes.length()").value(1))
        .andExpect(jsonPath("$.recipes[0].title").value("Chicken Pasta"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return empty results when no recipes match")
  void shouldReturnEmptyResultsWhenNoRecipesMatch() throws Exception {
    // Given
    searchRequest.setRecipeNameQuery("NonExistentRecipe");

    Page<Recipe> emptyPage = new PageImpl<>(Arrays.asList(),
        org.springframework.data.domain.PageRequest.of(0, 20), 0);

    when(recipeRepository.searchRecipes(
        eq("NonExistentRecipe"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(new String[0]),
        eq(new String[0]),
        any(Pageable.class)))
        .thenReturn(emptyPage);

    // When & Then
    mockMvc.perform(post("/recipes/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle pagination correctly")
  void shouldHandlePaginationCorrectly() throws Exception {
    // Given
    Page<Recipe> paginatedPage = new PageImpl<>(Arrays.asList(testRecipe2),
        org.springframework.data.domain.PageRequest.of(1, 1), 2);

    when(recipeRepository.searchRecipes(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(new String[0]),
        eq(new String[0]),
        any(Pageable.class)))
        .thenReturn(paginatedPage);

    // When & Then
    mockMvc.perform(post("/recipes/search?page=1&size=1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.last").value(true));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty search criteria")
  void shouldHandleEmptySearchCriteria() throws Exception {
    // Given - empty search request
    Page<Recipe> allRecipes = new PageImpl<>(Arrays.asList(testRecipe1, testRecipe2),
        org.springframework.data.domain.PageRequest.of(0, 20), 2);

    when(recipeRepository.searchRecipes(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(new String[0]),
        eq(new String[0]),
        any(Pageable.class)))
        .thenReturn(allRecipes);

    // When & Then
    mockMvc.perform(post("/recipes/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2));
  }
}
