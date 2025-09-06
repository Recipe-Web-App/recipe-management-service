package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET /recipe-management/recipes endpoint.
 * Tests the actual RecipeService getAllRecipes logic with mocked repository
 * calls.
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
class GetAllRecipesComponentTest extends AbstractComponentTest {

  private Recipe recipe1;
  private Recipe recipe2;

  @BeforeEach
  public void setUp() {
    super.setUp();
    useRealRecipeService(); // Use real service with mocked repositories

    recipe1 = new Recipe();
    recipe1.setRecipeId(1L);
    recipe1.setUserId(UUID.randomUUID());
    recipe1.setTitle("Spaghetti Carbonara");
    recipe1.setDescription("Classic Italian pasta dish");
    recipe1.setServings(BigDecimal.valueOf(4));
    recipe1.setPreparationTime(15);
    recipe1.setCookingTime(20);
    recipe1.setDifficulty(DifficultyLevel.MEDIUM);

    recipe2 = new Recipe();
    recipe2.setRecipeId(2L);
    recipe2.setUserId(UUID.randomUUID());
    recipe2.setTitle("Chicken Stir Fry");
    recipe2.setDescription("Quick and healthy stir fry");
    recipe2.setServings(BigDecimal.valueOf(2));
    recipe2.setPreparationTime(10);
    recipe2.setCookingTime(15);
    recipe2.setDifficulty(DifficultyLevel.EASY);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes should return paginated recipes successfully")
  void shouldReturnPaginatedRecipesSuccessfully() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(recipe1, recipe2), pageable, 2);
    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);

    // When & Then
    mockMvc.perform(get("/recipes")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(2))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
        .andExpect(jsonPath("$.recipes[0].title").value("Spaghetti Carbonara"))
        .andExpect(jsonPath("$.recipes[0].description").value("Classic Italian pasta dish"))
        .andExpect(jsonPath("$.recipes[0].servings").value(4))
        .andExpect(jsonPath("$.recipes[0].preparationTime").value(15))
        .andExpect(jsonPath("$.recipes[0].cookingTime").value(20))
        .andExpect(jsonPath("$.recipes[0].difficulty").value("MEDIUM"))
        .andExpect(jsonPath("$.recipes[1].recipeId").value(2))
        .andExpect(jsonPath("$.recipes[1].title").value("Chicken Stir Fry"))
        .andExpect(jsonPath("$.recipes[1].description").value("Quick and healthy stir fry"))
        .andExpect(jsonPath("$.recipes[1].servings").value(2))
        .andExpect(jsonPath("$.recipes[1].preparationTime").value(10))
        .andExpect(jsonPath("$.recipes[1].cookingTime").value(15))
        .andExpect(jsonPath("$.recipes[1].difficulty").value("EASY"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(2))
        .andExpect(jsonPath("$.empty").value(false));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes should return empty page when no recipes exist")
  void shouldReturnEmptyPageWhenNoRecipesExist() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

    // When & Then
    mockMvc.perform(get("/recipes")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(0))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(0))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes should handle pagination parameters correctly")
  void shouldHandlePaginationParametersCorrectly() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(1, 1);
    Page<Recipe> pagedRecipes = new PageImpl<>(Arrays.asList(recipe2), pageable, 2);
    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(pagedRecipes);

    // When & Then
    mockMvc.perform(get("/recipes")
        .param("page", "1")
        .param("size", "1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(1))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(2))
        .andExpect(jsonPath("$.recipes[0].title").value("Chicken Stir Fry"))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(1))
        .andExpect(jsonPath("$.empty").value(false));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes should use default pagination when no parameters provided")
  void shouldUseDefaultPaginationWhenNoParametersProvided() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20); // Default values
    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(recipe1), pageable, 1);
    when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);

    // When & Then
    mockMvc.perform(get("/recipes")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(1))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(1))
        .andExpect(jsonPath("$.empty").value(false));
  }
}
