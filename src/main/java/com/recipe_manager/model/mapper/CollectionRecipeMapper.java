package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.recipe.Recipe;

/** MapStruct mapper for mapping collection items with recipe data to CollectionRecipeDto. */
@Mapper(componentModel = "spring")
public interface CollectionRecipeMapper {
  /**
   * Maps a RecipeCollectionItem and Recipe to a CollectionRecipeDto.
   *
   * @param item the RecipeCollectionItem entity containing collection metadata
   * @param recipe the Recipe entity containing recipe information
   * @return the mapped CollectionRecipeDto, or null if either parameter is null
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  @Mapping(target = "recipeTitle", source = "recipe.title")
  @Mapping(target = "recipeDescription", source = "recipe.description")
  @Mapping(target = "displayOrder", source = "item.displayOrder")
  @Mapping(target = "addedBy", source = "item.addedBy")
  @Mapping(target = "addedAt", source = "item.addedAt")
  default CollectionRecipeDto toDto(RecipeCollectionItem item, Recipe recipe) {
    if (item == null || recipe == null) {
      return null;
    }
    return mapToDto(item, recipe);
  }

  /**
   * Internal mapping method used by the default toDto method.
   *
   * @param item the RecipeCollectionItem entity
   * @param recipe the Recipe entity
   * @return the mapped CollectionRecipeDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  @Mapping(target = "recipeTitle", source = "recipe.title")
  @Mapping(target = "recipeDescription", source = "recipe.description")
  @Mapping(target = "displayOrder", source = "item.displayOrder")
  @Mapping(target = "addedBy", source = "item.addedBy")
  @Mapping(target = "addedAt", source = "item.addedAt")
  CollectionRecipeDto mapToDto(RecipeCollectionItem item, Recipe recipe);
}
