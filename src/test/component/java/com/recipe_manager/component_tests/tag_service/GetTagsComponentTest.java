package com.recipe_manager.component_tests.tag_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/tags endpoint.
 * Tests the actual TagService getTags logic with mocked repository calls.
 */
@SpringBootTest(classes = {RecipeTagMapperImpl.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class GetTagsComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeTag italianTag;
  private RecipeTag vegetarianTag;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealTagService(); // Use real service with mocked repositories

    // Create test tags
    italianTag = RecipeTag.builder()
        .tagId(1L)
        .name("Italian")
        .build();

    vegetarianTag = RecipeTag.builder()
        .tagId(2L)
        .name("Vegetarian")
        .build();

    // Create test recipe with tags
    testRecipe = Recipe.builder()
        .recipeId(123L)
        .title("Test Recipe")
        .description("A test recipe")
        .recipeTags(Arrays.asList(italianTag, vegetarianTag))
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/tags should return tags successfully")
  void shouldGetTagsSuccessfully() throws Exception {
    // Given
    when(recipeRepository.findById(123L)).thenReturn(Optional.of(testRecipe));

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/tags"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(2))
        .andExpect(jsonPath("$.tags[0].tagId").value(1))
        .andExpect(jsonPath("$.tags[0].name").value("Italian"))
        .andExpect(jsonPath("$.tags[1].tagId").value(2))
        .andExpect(jsonPath("$.tags[1].name").value("Vegetarian"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/tags should return empty list for recipe with no tags")
  void shouldReturnEmptyTagList() throws Exception {
    // Given
    Recipe recipeWithoutTags = Recipe.builder()
        .recipeId(456L)
        .title("Recipe Without Tags")
        .description("A recipe with no tags")
        .recipeTags(Collections.emptyList())
        .build();

    when(recipeRepository.findById(456L)).thenReturn(Optional.of(recipeWithoutTags));

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/456/tags"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(0))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("GET /recipes/{recipeId}/tags should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/999/tags"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/tags should handle single tag")
  void shouldHandleSingleTag() throws Exception {
    // Given
    Recipe recipeWithSingleTag = Recipe.builder()
        .recipeId(789L)
        .title("Single Tag Recipe")
        .description("A recipe with one tag")
        .recipeTags(Arrays.asList(italianTag))
        .build();

    when(recipeRepository.findById(789L)).thenReturn(Optional.of(recipeWithSingleTag));

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/789/tags"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(789))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(1))
        .andExpect(jsonPath("$.tags[0].tagId").value(1))
        .andExpect(jsonPath("$.tags[0].name").value("Italian"))
        .andExpect(header().exists("X-Request-ID"));
  }
}
