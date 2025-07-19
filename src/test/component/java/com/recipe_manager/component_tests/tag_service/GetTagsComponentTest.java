package com.recipe_manager.component_tests.tag_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/tags endpoint.
 */
@Tag("component")
class GetTagsComponentTest extends AbstractComponentTest {
  @Mock
  private TagService tagService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(tagService.getTags(anyString())).thenReturn(ResponseEntity.ok("Get Tags - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get tags for a valid recipe ID")
  void shouldGetTags() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/tags")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Get Tags - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when getting tags")
  void shouldHandleNotFoundForNonExistentRecipeTags() throws Exception {
    when(tagService.getTags("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipe-management/recipes/nonexistent/tags")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
