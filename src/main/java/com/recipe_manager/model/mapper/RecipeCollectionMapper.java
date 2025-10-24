package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.recipe_manager.model.dto.collection.RecipeCollectionDto;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.entity.collection.RecipeCollection;

/** MapStruct mapper for RecipeCollection entity to DTO conversions. */
@Mapper(
    componentModel = "spring",
    uses = {RecipeCollectionItemMapper.class, CollectionCollaboratorMapper.class})
public interface RecipeCollectionMapper {
  /**
   * Maps a RecipeCollection entity to a RecipeCollectionDto.
   *
   * @param collection the RecipeCollection entity
   * @return the mapped RecipeCollectionDto
   */
  @Mapping(target = "items", source = "collectionItems")
  @Mapping(target = "collaborators", source = "collaborators")
  RecipeCollectionDto toDto(RecipeCollection collection);

  /**
   * Updates a RecipeCollection entity from an UpdateCollectionRequest. Only non-null fields in the
   * request will be updated.
   *
   * @param request the update collection request DTO
   * @param collection the collection entity to update
   */
  @Mapping(target = "collectionId", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "collectionItems", ignore = true)
  @Mapping(target = "collaborators", ignore = true)
  void updateCollectionFromRequest(
      UpdateCollectionRequest request, @MappingTarget RecipeCollection collection);
}
