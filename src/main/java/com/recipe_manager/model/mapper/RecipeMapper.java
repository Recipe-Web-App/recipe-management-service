package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.entity.recipe.Recipe;

/** MapStruct mapper for updating Recipe from UpdateRecipeRequest, including nested collections. */
@Mapper(
    componentModel = "spring",
    uses = {
      RecipeIngredientMapper.class,
      RecipeStepMapper.class,
      RecipeFavoriteMapper.class,
      RecipeRevisionMapper.class,
      RecipeTagMapper.class,
      RecipeCommentMapper.class
    })
public interface RecipeMapper {
  /**
   * Maps a Recipe entity to a RecipeDto.
   *
   * @param recipe the Recipe entity
   * @return the mapped RecipeDto
   */
  @Mapping(target = "ingredients", source = "recipeIngredients")
  @Mapping(target = "steps", source = "recipeSteps")
  @Mapping(target = "favorites", source = "recipeFavorites")
  @Mapping(target = "revisions", source = "recipeRevisions")
  @Mapping(target = "tags", source = "recipeTags")
  @Mapping(target = "comments", source = "recipeComments")
  RecipeDto toDto(Recipe recipe);

  /**
   * Updates the given Recipe entity with values from the UpdateRecipeRequest. Only non-null fields
   * in the request will be updated.
   *
   * @param request the update recipe request DTO
   * @param recipe the recipe entity to update
   */
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "recipeId", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "recipeFavorites", ignore = true)
  @Mapping(target = "recipeRevisions", ignore = true)
  @Mapping(target = "recipeTags", ignore = true)
  @Mapping(target = "recipeComments", ignore = true)
  @Mapping(target = "recipeIngredients", source = "ingredients")
  @Mapping(target = "recipeSteps", source = "steps")
  void updateRecipeFromRequest(UpdateRecipeRequest request, @MappingTarget Recipe recipe);
}
