package com.recipe_manager.component_tests;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.RecipeManagementController;
import com.recipe_manager.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class AbstractComponentTest {
  protected MockMvc mockMvc;
  @InjectMocks
  protected RecipeManagementController controller;

  @BeforeEach
  protected void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .addFilters(new RequestIdFilter())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }
}
