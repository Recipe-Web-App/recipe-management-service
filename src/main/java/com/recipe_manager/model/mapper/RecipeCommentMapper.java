package com.recipe_manager.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.recipe_manager.model.dto.recipe.RecipeCommentDto;
import com.recipe_manager.model.entity.recipe.RecipeComment;

/** MapStruct mapper for converting between RecipeComment entities and DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecipeCommentMapper {

  /**
   * Convert RecipeComment entity to DTO.
   *
   * @param entity the entity to convert
   * @return the DTO
   */
  @Mapping(source = "recipe.recipeId", target = "recipeId")
  RecipeCommentDto toDto(RecipeComment entity);

  /**
   * Convert list of RecipeComment entities to DTOs.
   *
   * @param entities the entities to convert
   * @return the DTOs
   */
  List<RecipeCommentDto> toDtoList(List<RecipeComment> entities);

  /**
   * Convert RecipeCommentDto to entity.
   *
   * @param dto the DTO to convert
   * @return the entity
   */
  @Mapping(target = "recipe", ignore = true)
  RecipeComment toEntity(RecipeCommentDto dto);
}
