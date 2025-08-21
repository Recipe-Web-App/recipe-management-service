package com.recipe_manager.unit_tests.model.revision;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.revision.AbstractRevision;
import com.recipe_manager.model.dto.revision.IngredientAddRevision;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.Test;

class DebugJsonTest {

  @Test
  void debugJson() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    IngredientAddRevision revision = IngredientAddRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Flour")
        .quantity(new BigDecimal("2.5"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .description("All-purpose flour")
        .build();

    // Test concrete type serialization
    String jsonConcrete = objectMapper.writeValueAsString(revision);
    System.out.println("Concrete JSON: " + jsonConcrete);

    // Test abstract type serialization
    String jsonAbstract = objectMapper.writeValueAsString((AbstractRevision) revision);
    System.out.println("Abstract JSON: " + jsonAbstract);

    // Test deserialization
    try {
      AbstractRevision deserialized = objectMapper.readValue(jsonAbstract, AbstractRevision.class);
      System.out.println("Deserialized class: " + deserialized.getClass().getSimpleName());
    } catch (Exception e) {
      System.out.println("Deserialization error: " + e.getMessage());
    }
  }
}
