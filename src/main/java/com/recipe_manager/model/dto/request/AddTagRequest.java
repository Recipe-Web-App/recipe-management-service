package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request DTO for adding a tag to a recipe. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AddTagRequest {
  /** Max name length as defined in DB schema. */
  private static final int MAX_NAME_LENGTH = 50;

  /** The name of the tag to add. */
  @NotBlank(message = "Tag name cannot be blank")
  @Size(
      max = MAX_NAME_LENGTH,
      message = "Tag name cannot exceed " + MAX_NAME_LENGTH + " characters")
  private String name;
}
