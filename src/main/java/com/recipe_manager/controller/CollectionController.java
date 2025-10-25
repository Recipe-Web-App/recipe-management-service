package com.recipe_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.service.CollectionService;

/**
 * REST controller for Recipe Collection API endpoints.
 *
 * <p>Provides endpoints for managing recipe collections including retrieval, creation, updates, and
 * managing collaborators.
 */
@RestController
@RequestMapping("/collections")
public class CollectionController {

  /** Default page size for pagination. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** Service for collection operations. */
  private final CollectionService collectionService;

  /**
   * Constructs the controller with required services.
   *
   * @param collectionService the collection service
   */
  public CollectionController(final CollectionService collectionService) {
    this.collectionService = collectionService;
  }

  /**
   * Get all collections accessible to the authenticated user.
   *
   * <p>Returns collections that the user owns, collaborates on, or are publicly accessible. Results
   * are paginated and can be sorted.
   *
   * @param pageable pagination parameters (page, size, sort)
   * @return ResponseEntity with paginated list of collections
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<CollectionDto>> getCollections(
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    return collectionService.getAccessibleCollections(pageable);
  }
}
