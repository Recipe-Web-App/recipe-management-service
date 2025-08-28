package com.recipe_manager.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response DTO for media deletion operations. Contains confirmation of the deletion operation. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DeleteMediaResponse {
  /** Whether the deletion was successful. */
  private boolean success;

  /** Human-readable message about the deletion result. */
  private String message;

  /** The ID of the deleted media. */
  private Long mediaId;
}
