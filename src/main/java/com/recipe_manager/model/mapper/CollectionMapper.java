package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.CollectionSummaryResponse;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;

/** MapStruct mapper for converting collection response objects. */
@Mapper(componentModel = "spring")
public interface CollectionMapper {
  /**
   * Converts CollectionSummaryResponse (from database view) to CollectionDto (for API response).
   * Maps ownerId field to userId to match OpenAPI specification.
   *
   * @param summary the collection summary from database view
   * @return the API response DTO
   */
  @Mapping(target = "userId", source = "ownerId")
  CollectionDto toDto(CollectionSummaryResponse summary);

  /**
   * Converts CollectionSummaryProjection (from native query) to CollectionDto (for API response).
   * Maps ownerId field to userId to match OpenAPI specification.
   *
   * @param projection the collection summary projection from native query
   * @return the API response DTO
   */
  @Mapping(target = "userId", source = "ownerId")
  CollectionDto fromProjection(CollectionSummaryProjection projection);

  /**
   * Converts RecipeCollection entity to CollectionDto (for API response). Calculates recipe count
   * and collaborator count from the entity's collections.
   *
   * @param collection the recipe collection entity
   * @return the API response DTO
   */
  @Mapping(
      target = "recipeCount",
      expression =
          "java(collection.getCollectionItems() != null ? collection.getCollectionItems().size() :"
              + " 0)")
  @Mapping(
      target = "collaboratorCount",
      expression =
          "java(collection.getCollaborators() != null ? collection.getCollaborators().size() : 0)")
  CollectionDto toDto(RecipeCollection collection);

  /**
   * Converts RecipeCollection entity to CollectionDetailsDto (for API response) including all
   * recipes and collaborators.
   *
   * @param collection the recipe collection entity with items and collaborators eagerly loaded
   * @return the detailed collection DTO with recipes and collaborators
   */
  @Mapping(target = "recipes", source = "collectionItems")
  @Mapping(target = "collaborators", source = "collaborators")
  CollectionDetailsDto toDetailsDto(RecipeCollection collection);

  /**
   * Converts RecipeCollectionItem entity to CollectionRecipeDto. Extracts recipe information from
   * the recipe relationship.
   *
   * @param item the recipe collection item entity
   * @return the recipe DTO with display order and audit info
   */
  @Mapping(target = "recipeId", source = "id.recipeId")
  @Mapping(target = "recipeTitle", source = "recipe.title")
  @Mapping(target = "recipeDescription", source = "recipe.description")
  CollectionRecipeDto toRecipeDto(RecipeCollectionItem item);

  /**
   * Converts a list of RecipeCollectionItem entities to a list of CollectionRecipeDto.
   *
   * @param items the list of recipe collection items
   * @return the list of recipe DTOs
   */
  List<CollectionRecipeDto> toRecipeDtoList(List<RecipeCollectionItem> items);

  /**
   * Converts CollectionCollaborator entity to CollectionCollaboratorDto.
   *
   * @param collaborator the collection collaborator entity
   * @return the collaborator DTO
   */
  @Mapping(target = "collectionId", source = "id.collectionId")
  @Mapping(target = "userId", source = "id.userId")
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "grantedByUsername", ignore = true)
  CollectionCollaboratorDto toCollaboratorDto(CollectionCollaborator collaborator);

  /**
   * Converts a list of CollectionCollaborator entities to a list of CollectionCollaboratorDto.
   *
   * @param collaborators the list of collection collaborators
   * @return the list of collaborator DTOs
   */
  List<CollectionCollaboratorDto> toCollaboratorDtoList(List<CollectionCollaborator> collaborators);
}
