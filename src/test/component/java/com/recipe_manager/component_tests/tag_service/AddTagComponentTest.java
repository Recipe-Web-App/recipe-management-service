package com.recipe_manager.component_tests.tag_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Component tests for POST /recipe-management/recipes/{recipeId}/tags endpoint.
 * Tests the actual TagService addTag logic with mocked repository calls.
 */
@SpringBootTest(classes = {RecipeTagMapperImpl.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class AddTagComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeTag italianTag;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealTagService(); // Use real service with mocked repositories

    // Create test recipe without tags initially
    testRecipe = Recipe.builder()
        .recipeId(123L)
        .title("Test Recipe")
        .description("A test recipe")
        .recipeTags(Collections.emptyList())
        .build();

    // Create test tag
    italianTag = RecipeTag.builder()
        .tagId(1L)
        .name("Italian")
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should add new tag successfully")
  void shouldAddNewTagSuccessfully() throws Exception {
    // Given
    String tagName = "Italian";
    RecipeTag newTag = RecipeTag.builder()
        .tagId(1L)
        .name(tagName)
        .build();

    when(recipeRepository.findById(123L)).thenReturn(Optional.of(testRecipe));
    when(recipeTagRepository.findByNameIgnoreCase(tagName)).thenReturn(Optional.empty());
    when(recipeTagRepository.save(any(RecipeTag.class))).thenReturn(newTag);

    String requestBody = """
        {
          "name": "%s"
        }
        """.formatted(tagName);

    // When & Then
    mockMvc.perform(post("/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(1))
        .andExpect(jsonPath("$.tags[0].tagId").value(1))
        .andExpect(jsonPath("$.tags[0].name").value("Italian"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should reuse existing tag")
  void shouldReuseExistingTag() throws Exception {
    // Given
    String tagName = "Italian";
    Recipe recipeWithExistingTags = Recipe.builder()
        .recipeId(456L)
        .title("Recipe with existing tags")
        .description("A recipe with existing tags")
        .recipeTags(Collections.emptyList())
        .build();

    when(recipeRepository.findById(456L)).thenReturn(Optional.of(recipeWithExistingTags));
    when(recipeTagRepository.findByNameIgnoreCase(tagName)).thenReturn(Optional.of(italianTag));

    String requestBody = """
        {
          "name": "%s"
        }
        """.formatted(tagName);

    // When & Then
    mockMvc.perform(post("/recipes/456/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.recipeId").value(456))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(1))
        .andExpect(jsonPath("$.tags[0].tagId").value(1))
        .andExpect(jsonPath("$.tags[0].name").value("Italian"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "name": "Italian"
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipes/999/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should return 400 for invalid request")
  void shouldHandleInvalidRequest() throws Exception {
    // Given - empty tag name
    String requestBody = """
        {
          "name": ""
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should return 400 for missing name field")
  void shouldHandleMissingNameField() throws Exception {
    // Given - missing name field
    String requestBody = """
        {
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("POST /recipes/{recipeId}/tags should handle adding tag to recipe with existing tags")
  void shouldAddTagToRecipeWithExistingTags() throws Exception {
    // Given
    String newTagName = "Vegetarian";
    RecipeTag vegetarianTag = RecipeTag.builder()
        .tagId(2L)
        .name(newTagName)
        .build();

    Recipe recipeWithTags = Recipe.builder()
        .recipeId(789L)
        .title("Recipe with tags")
        .description("A recipe with existing tags")
        .recipeTags(Arrays.asList(italianTag))
        .build();

    when(recipeRepository.findById(789L)).thenReturn(Optional.of(recipeWithTags));
    when(recipeTagRepository.findByNameIgnoreCase(newTagName)).thenReturn(Optional.empty());
    when(recipeTagRepository.save(any(RecipeTag.class))).thenReturn(vegetarianTag);

    String requestBody = """
        {
          "name": "%s"
        }
        """.formatted(newTagName);

    // When & Then
    mockMvc.perform(post("/recipes/789/tags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.recipeId").value(789))
        .andExpect(jsonPath("$.tags").isArray())
        .andExpect(jsonPath("$.tags.length()").value(2))
        .andExpect(header().exists("X-Request-ID"));
  }
}
