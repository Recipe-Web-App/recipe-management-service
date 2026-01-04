package com.recipe_manager.repository.collection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.collection.CollectionTag;

/**
 * Repository interface for CollectionTag entity. Provides CRUD operations and custom query methods
 * for collection tags.
 */
@Repository
public interface CollectionTagRepository extends JpaRepository<CollectionTag, Long> {

  /**
   * Find a tag by its name (case insensitive).
   *
   * @param name the tag name to search for
   * @return Optional containing the tag if found, empty otherwise
   */
  Optional<CollectionTag> findByNameIgnoreCase(String name);

  /**
   * Check if a tag exists by name (case insensitive).
   *
   * @param name the tag name to check
   * @return true if tag exists, false otherwise
   */
  boolean existsByNameIgnoreCase(String name);
}
