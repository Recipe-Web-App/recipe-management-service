package com.recipe_manager.model.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Request DTO for updating an existing recipe. All fields are optional. */
@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UpdateRecipeRequest extends AbstractRecipeRequest {
  // Fields inherited from AbstractRecipeRequest are optional and can be null.
}
