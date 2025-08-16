package com.recipe_manager.unit_tests.enums;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.enums.ExternalServiceName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExternalServiceNameTest {

  @Test
  @DisplayName("Should return correct service name for RECIPE_SCRAPER")
  void shouldReturnCorrectServiceNameForRecipeScraper() {
    assertThat(ExternalServiceName.RECIPE_SCRAPER.getServiceName())
        .isEqualTo("recipe-scraper");
  }

  @Test
  @DisplayName("Should have expected enum values")
  void shouldHaveExpectedEnumValues() {
    ExternalServiceName[] values = ExternalServiceName.values();

    assertThat(values).hasSize(4);
    assertThat(values).contains(ExternalServiceName.RECIPE_SCRAPER);
  }

  @Test
  @DisplayName("Should support valueOf")
  void shouldSupportValueOf() {
    ExternalServiceName value = ExternalServiceName.valueOf("RECIPE_SCRAPER");

    assertThat(value).isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
    assertThat(value.getServiceName()).isEqualTo("recipe-scraper");
  }
}
