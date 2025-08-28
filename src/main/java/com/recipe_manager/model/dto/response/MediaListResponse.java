package com.recipe_manager.model.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.recipe_manager.model.dto.media.MediaSummaryDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for paginated media listings. Contains the list of media items along with pagination
 * metadata.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MediaListResponse {
  /** List of media items in this page. */
  @NotNull @Valid private List<MediaSummaryDto> items;

  /** Total number of items across all pages. */
  @Min(0)
  private Long totalCount;

  /** Current page number (0-based). */
  @Min(0)
  private Integer page;

  /** Number of items per page. */
  @Min(1)
  private Integer size;

  /** Whether there are more pages available. */
  private Boolean hasNext;

  /** Whether there are previous pages available. */
  private Boolean hasPrevious;
}
