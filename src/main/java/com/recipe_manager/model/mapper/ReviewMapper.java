package com.recipe_manager.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.review.ReviewDto;
import com.recipe_manager.model.entity.review.Review;

/** MapStruct mapper for Review entity and ReviewDto conversions. */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

  /**
   * Maps a Review entity to a ReviewDto.
   *
   * @param review the Review entity
   * @return the mapped ReviewDto
   */
  @Mapping(target = "recipeId", source = "recipe.recipeId")
  ReviewDto toDto(Review review);

  /**
   * Maps a ReviewDto to a Review entity.
   *
   * @param reviewDto the ReviewDto
   * @return the mapped Review entity
   */
  @Mapping(target = "recipe", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Review toEntity(ReviewDto reviewDto);
}
