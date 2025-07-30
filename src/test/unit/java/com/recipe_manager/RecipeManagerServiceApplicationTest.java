package com.recipe_manager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

/**
 * Test for RecipeManagerServiceApplication main method.
 */
@Tag("unit")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
class RecipeManagerServiceApplicationTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should verify class exists and has main method")
  void shouldVerifyClassStructure() {
    // Verify class exists
    assertNotNull(RecipeManagerServiceApplication.class);

    // Verify main method exists
    assertDoesNotThrow(() -> {
      var mainMethod = RecipeManagerServiceApplication.class.getMethod("main", String[].class);
      assertNotNull(mainMethod);
    });
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should execute main method and verify SpringApplication.run is called")
  void shouldExecuteMainMethod() {
    // Given
    String[] args = { "--test" };

    // Mock SpringApplication.run to avoid actual application startup
    try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
      ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
      springApplicationMock.when(() -> SpringApplication.run(RecipeManagerServiceApplication.class, args))
          .thenReturn(mockContext);

      // When & Then - should not throw any exceptions
      assertDoesNotThrow(() -> {
        RecipeManagerServiceApplication.main(args);
      });

      // Verify SpringApplication.run was called with correct parameters
      springApplicationMock.verify(() -> SpringApplication.run(RecipeManagerServiceApplication.class, args));
    }
  }
}
