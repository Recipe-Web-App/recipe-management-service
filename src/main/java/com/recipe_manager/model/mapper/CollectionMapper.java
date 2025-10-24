package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.CollectionSummaryResponse;

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
  @Mapping(target = "collectionId", source = "collectionId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "visibility", source = "visibility")
  @Mapping(target = "collaborationMode", source = "collaborationMode")
  @Mapping(target = "recipeCount", source = "recipeCount")
  @Mapping(target = "collaboratorCount", source = "collaboratorCount")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  CollectionDto toDto(CollectionSummaryResponse summary);
}
