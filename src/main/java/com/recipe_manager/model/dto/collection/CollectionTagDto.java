package com.recipe_manager.model.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for CollectionTag entity. Used for transferring collection tag data between
 * layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class CollectionTagDto {
  /** The tag ID. */
  private Long tagId;

  /** The tag name. */
  private String name;
}
