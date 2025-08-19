package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.dto.request.AddTagRequest;
import com.recipe_manager.model.dto.request.RemoveTagRequest;
import com.recipe_manager.model.dto.response.TagResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.mapper.RecipeTagMapper;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;

/**
 * Unit tests for {@link TagService}.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private RecipeTagRepository recipeTagRepository;

  @Mock
  private RecipeTagMapper recipeTagMapper;

  private TagService tagService;

  @BeforeEach
  void setUp() {
    tagService = new TagService(recipeRepository, recipeTagRepository, recipeTagMapper);
  }

  @Test
  @DisplayName("addTag should create new tag and add to recipe")
  void addTag_shouldCreateNewTagAndAddToRecipe() {
    // Given
    Long recipeId = 1L;
    AddTagRequest request = AddTagRequest.builder().name("Italian").build();
    Recipe recipe = Recipe.builder().recipeId(recipeId).recipeTags(Collections.emptyList()).build();
    RecipeTag newTag = RecipeTag.builder().tagId(1L).name("Italian").build();
    List<RecipeTagDto> tagDtos = Arrays.asList(RecipeTagDto.builder().tagId(1L).name("Italian").build());

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeTagRepository.findByNameIgnoreCase("Italian")).thenReturn(Optional.empty());
    when(recipeTagRepository.save(any(RecipeTag.class))).thenReturn(newTag);
    when(recipeTagMapper.toDtoList(any())).thenReturn(tagDtos);

    // When
    TagResponse response = tagService.addTag(recipeId, request);

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(1, response.getTags().size());
    assertEquals("Italian", response.getTags().get(0).getName());
    verify(recipeTagRepository).save(any(RecipeTag.class));
    verify(recipeRepository).save(recipe);
  }

  @Test
  @DisplayName("addTag should use existing tag if it exists")
  void addTag_shouldUseExistingTagIfExists() {
    // Given
    Long recipeId = 1L;
    AddTagRequest request = AddTagRequest.builder().name("Italian").build();
    Recipe recipe = Recipe.builder().recipeId(recipeId).recipeTags(Collections.emptyList()).build();
    RecipeTag existingTag = RecipeTag.builder().tagId(1L).name("Italian").build();
    List<RecipeTagDto> tagDtos = Arrays.asList(RecipeTagDto.builder().tagId(1L).name("Italian").build());

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeTagRepository.findByNameIgnoreCase("Italian")).thenReturn(Optional.of(existingTag));
    when(recipeTagMapper.toDtoList(any())).thenReturn(tagDtos);

    // When
    TagResponse response = tagService.addTag(recipeId, request);

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(1, response.getTags().size());
    verify(recipeRepository).save(recipe);
  }

  @Test
  @DisplayName("addTag should throw exception for non-existent recipe")
  void addTag_shouldThrowExceptionForNonExistentRecipe() {
    // Given
    Long recipeId = 999L;
    AddTagRequest request = AddTagRequest.builder().name("Italian").build();

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> tagService.addTag(recipeId, request));
  }

  @Test
  @DisplayName("removeTag should remove tag from recipe")
  void removeTag_shouldRemoveTagFromRecipe() {
    // Given
    Long recipeId = 1L;
    RemoveTagRequest request = RemoveTagRequest.builder().tagName("Italian").build();
    RecipeTag tag = RecipeTag.builder().tagId(1L).name("Italian").build();
    Recipe recipe = Recipe.builder().recipeId(recipeId).recipeTags(Arrays.asList(tag)).build();

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeTagRepository.findByNameIgnoreCase("Italian")).thenReturn(Optional.of(tag));
    when(recipeTagMapper.toDtoList(any())).thenReturn(Collections.emptyList());

    // When
    TagResponse response = tagService.removeTag(recipeId, request);

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertTrue(response.getTags().isEmpty());
    verify(recipeRepository).save(recipe);
  }

  @Test
  @DisplayName("removeTag should throw exception for non-existent tag")
  void removeTag_shouldThrowExceptionForNonExistentTag() {
    // Given
    Long recipeId = 1L;
    RemoveTagRequest request = RemoveTagRequest.builder().tagName("NonExistent").build();
    Recipe recipe = Recipe.builder().recipeId(recipeId).recipeTags(Collections.emptyList()).build();

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeTagRepository.findByNameIgnoreCase("NonExistent")).thenReturn(Optional.empty());

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> tagService.removeTag(recipeId, request));
  }

  @Test
  @DisplayName("getTags should return all tags for recipe")
  void getTags_shouldReturnAllTagsForRecipe() {
    // Given
    Long recipeId = 1L;
    List<RecipeTag> tags = Arrays.asList(
        RecipeTag.builder().tagId(1L).name("Italian").build(),
        RecipeTag.builder().tagId(2L).name("Vegetarian").build()
    );
    Recipe recipe = Recipe.builder().recipeId(recipeId).recipeTags(tags).build();
    List<RecipeTagDto> tagDtos = Arrays.asList(
        RecipeTagDto.builder().tagId(1L).name("Italian").build(),
        RecipeTagDto.builder().tagId(2L).name("Vegetarian").build()
    );

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeTagMapper.toDtoList(tags)).thenReturn(tagDtos);

    // When
    TagResponse response = tagService.getTags(recipeId);

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(2, response.getTags().size());
    assertEquals("Italian", response.getTags().get(0).getName());
    assertEquals("Vegetarian", response.getTags().get(1).getName());
  }

  @Test
  @DisplayName("getTags should throw exception for non-existent recipe")
  void getTags_shouldThrowExceptionForNonExistentRecipe() {
    // Given
    Long recipeId = 999L;

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> tagService.getTags(recipeId));
  }
}
