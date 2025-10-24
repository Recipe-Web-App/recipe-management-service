package com.recipe_manager.repository.collection;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;

/**
 * Repository interface for RecipeCollectionItem entity operations. Handles recipe-to-collection
 * associations with custom ordering.
 */
@Repository
public interface RecipeCollectionItemRepository
    extends JpaRepository<RecipeCollectionItem, RecipeCollectionItemId> {

  /**
   * Finds all items in a collection, ordered by display order.
   *
   * @param collectionId the collection ID
   * @return list of collection items in display order
   */
  List<RecipeCollectionItem> findByIdCollectionIdOrderByDisplayOrderAsc(Long collectionId);

  /**
   * Finds all items in a collection (unordered).
   *
   * @param collectionId the collection ID
   * @return list of collection items
   */
  List<RecipeCollectionItem> findByIdCollectionId(Long collectionId);

  /**
   * Finds all collections containing a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of collection items for the recipe
   */
  List<RecipeCollectionItem> findByIdRecipeId(Long recipeId);

  /**
   * Finds a specific item in a collection by composite key components.
   *
   * @param collectionId the collection ID
   * @param recipeId the recipe ID
   * @return optional containing the item if found
   */
  Optional<RecipeCollectionItem> findByIdCollectionIdAndIdRecipeId(
      Long collectionId, Long recipeId);

  /**
   * Checks if a recipe exists in a collection.
   *
   * @param collectionId the collection ID
   * @param recipeId the recipe ID
   * @return true if recipe is in collection
   */
  boolean existsByIdCollectionIdAndIdRecipeId(Long collectionId, Long recipeId);

  /**
   * Deletes a recipe from a collection by composite key components.
   *
   * @param collectionId the collection ID
   * @param recipeId the recipe ID
   */
  void deleteByIdCollectionIdAndIdRecipeId(Long collectionId, Long recipeId);

  /**
   * Counts the number of recipes in a collection.
   *
   * @param collectionId the collection ID
   * @return count of recipes in the collection
   */
  long countByIdCollectionId(Long collectionId);

  /**
   * Finds the maximum display order value in a collection. Useful for adding new items at the end.
   *
   * @param collectionId the collection ID
   * @return the maximum display order, or null if collection is empty
   */
  @Query(
      "SELECT MAX(rci.displayOrder) FROM RecipeCollectionItem rci WHERE rci.id.collectionId ="
          + " :collectionId")
  Integer findMaxDisplayOrderByCollectionId(@Param("collectionId") Long collectionId);

  /**
   * Finds items in multiple collections (batch operation).
   *
   * @param collectionIds list of collection IDs
   * @return list of collection items
   */
  List<RecipeCollectionItem> findByIdCollectionIdIn(List<Long> collectionIds);

  /**
   * Deletes all items from a collection.
   *
   * @param collectionId the collection ID
   */
  void deleteByIdCollectionId(Long collectionId);
}
