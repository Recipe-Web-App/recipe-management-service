package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.collection.CollectionTagDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response DTO for collection tag operations. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class CollectionTagResponse {
  /** The collection ID. */
  private Long collectionId;

  /** The list of tags for the collection. */
  private List<CollectionTagDto> tags;
}
