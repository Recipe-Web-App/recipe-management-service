package com.recipe_manager.component_tests.tag_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for DELETE /recipe-management/recipes/{recipeId}/tags endpoint.
 * Tests the actual TagService removeTag logic with mocked repository calls.
 */
@SpringBootTest(classes = {RecipeTagMapperImpl.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class RemoveTagComponentTest extends AbstractComponentTest {

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
  @DisplayName("DELETE /recipes/{recipeId}/tags should remove tag successfully")
  void shouldRemoveTagSuccessfully() throws Exception {
    // Given
    String tagName = "Italian";

    when(recipeRepository.findById(123L)).thenReturn(Optional.of(testRecipe));
    when(recipeTagRepository.findByNameIgnoreCase(tagName)).thenReturn(Optional.of(italianTag));

    String requestBody = """
        {
          "tagName": "%s"
        }
        """.formatted(tagName);

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(1))
        .andExpect(jsonPath("$.tags[0].tagId").value(2))
        .andExpect(jsonPath("$.tags[0].name").value("Vegetarian"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("DELETE /recipes/{recipeId}/tags should return empty list when removing last tag")
  void shouldReturnEmptyListWhenRemovingLastTag() throws Exception {
    // Given
    String tagName = "Italian";
    Recipe recipeWithSingleTag = Recipe.builder()
        .recipeId(456L)
        .title("Recipe with single tag")
        .description("A recipe with only one tag")
        .recipeTags(Arrays.asList(italianTag))
        .build();

    when(recipeRepository.findById(456L)).thenReturn(Optional.of(recipeWithSingleTag));
    when(recipeTagRepository.findByNameIgnoreCase(tagName)).thenReturn(Optional.of(italianTag));

    String requestBody = """
        {
          "tagName": "%s"
        }
        """.formatted(tagName);

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/456/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(0))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/tags should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "tagName": "Italian"
        }
        """;

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/999/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/tags should return 404 for non-existent tag")
  void shouldHandleNotFoundForNonExistentTag() throws Exception {
    // Given
    String tagName = "NonExistent";

    when(recipeRepository.findById(123L)).thenReturn(Optional.of(testRecipe));
    when(recipeTagRepository.findByNameIgnoreCase(tagName)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "tagName": "%s"
        }
        """.formatted(tagName);

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/tags should return 400 for invalid request")
  void shouldHandleInvalidRequest() throws Exception {
    // Given - empty tag name
    String requestBody = """
        {
          "tagName": ""
        }
        """;

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/tags should return 400 for missing tagName field")
  void shouldHandleMissingTagNameField() throws Exception {
    // Given - missing tagName field
    String requestBody = """
        {
        }
        """;

    // When & Then
    mockMvc.perform(delete("/recipe-management/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
