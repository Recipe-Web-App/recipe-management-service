package com.recipe_manager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/** Service for tag-related operations. */
@Service
public class TagService {

  /** Repository for managing recipe data. */
  private final RecipeRepository recipeRepository;

  /** Repository for managing recipe tag data. */
  private final RecipeTagRepository recipeTagRepository;

  /** Mapper for converting between RecipeTag entities and DTOs. */
  private final RecipeTagMapper recipeTagMapper;

  public TagService(
      final RecipeRepository recipeRepository,
      final RecipeTagRepository recipeTagRepository,
      final RecipeTagMapper recipeTagMapper) {
    this.recipeRepository = recipeRepository;
    this.recipeTagRepository = recipeTagRepository;
    this.recipeTagMapper = recipeTagMapper;
  }

  /**
   * Add a tag to a recipe.
   *
   * @param recipeId the recipe ID
   * @param request the add tag request
   * @return response with updated tags
   * @throws ResourceNotFoundException if recipe not found
   */
  @Transactional
  public TagResponse addTag(final Long recipeId, final AddTagRequest request) {
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with ID: " + recipeId));

    // Find or create the tag
    RecipeTag tag =
        recipeTagRepository
            .findByNameIgnoreCase(request.getName())
            .orElseGet(
                () -> {
                  RecipeTag newTag = RecipeTag.builder().name(request.getName().trim()).build();
                  return recipeTagRepository.save(newTag);
                });

    // Add tag to recipe if not already present
    if (!recipe.getRecipeTags().contains(tag)) {
      // Create a new mutable list if the current one is immutable
      List<RecipeTag> updatedTags = new ArrayList<>(recipe.getRecipeTags());
      updatedTags.add(tag);
      recipe.setRecipeTags(updatedTags);
      recipeRepository.save(recipe);
    }

    // Return updated tag list
    return getTagsResponse(recipeId, recipe.getRecipeTags());
  }

  /**
   * Remove a tag from a recipe.
   *
   * @param recipeId the recipe ID
   * @param request the remove tag request
   * @return response with updated tags
   * @throws ResourceNotFoundException if recipe or tag not found
   */
  @Transactional
  public TagResponse removeTag(final Long recipeId, final RemoveTagRequest request) {
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with ID: " + recipeId));

    RecipeTag tag =
        recipeTagRepository
            .findByNameIgnoreCase(request.getTagName())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Tag not found with name: " + request.getTagName()));

    // Remove tag from recipe
    List<RecipeTag> updatedTags = new ArrayList<>(recipe.getRecipeTags());
    updatedTags.remove(tag);
    recipe.setRecipeTags(updatedTags);
    recipeRepository.save(recipe);

    // Return updated tag list
    return getTagsResponse(recipeId, recipe.getRecipeTags());
  }

  /**
   * Get tags for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response with all tags for the recipe
   * @throws ResourceNotFoundException if recipe not found
   */
  public TagResponse getTags(final Long recipeId) {
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with ID: " + recipeId));

    return getTagsResponse(recipeId, recipe.getRecipeTags());
  }

  /**
   * Helper method to create TagResponse from recipe tags.
   *
   * @param recipeId the recipe ID
   * @param tags the list of recipe tags
   * @return TagResponse with mapped DTOs
   */
  private TagResponse getTagsResponse(final Long recipeId, final List<RecipeTag> tags) {
    List<RecipeTagDto> tagDtos = recipeTagMapper.toDtoList(tags);
    return TagResponse.builder().recipeId(recipeId).tags(tagDtos).build();
  }
}
