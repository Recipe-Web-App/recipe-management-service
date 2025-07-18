package com.recipe_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Recipe Manager Service Spring Boot application. */
@SpringBootApplication
public final class RecipeManagerServiceApplication {
  /** Private constructor to prevent instantiation. */
  private RecipeManagerServiceApplication() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(RecipeManagerServiceApplication.class, args);
  }
}
