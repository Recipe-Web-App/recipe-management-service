# Trending Recipes Algorithm

This document outlines the algorithm for determining "Trending Recipes".
The goal is to surface recipes that are currently popular based on user
engagement.

## Algorithm Overview

The "Trending Score" is calculated using a **Time-Decayed Weighted Sum** of
user interactions. This ensures that recent interactions have a much higher
impact than older ones, allowing new viral recipes to rise quickly while
preventing old popular recipes from staying at the top forever.

### Formula

$$ \text{Score} = \sum (\text{Interaction Value} \times e^{-\lambda \times \text{Age}}) $$

Where:

* $\text{Interaction Value}$: The weight assigned to a specific type of
    interaction.
* $\lambda$ (Lambda): The decay rate.
* $\text{Age}$: Time elapsed since the interaction occurred (in hours or
    days).

## Data Points & Weights

We will use the following data points available in the database.

<!-- markdownlint-disable MD013 -->
| Interaction Type | Database Table | Timestamp Column | Weight ($W$) | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Favorite** | `recipe_favorites` | `favorited_at` | **3.0** | High intent signal. |
| **Collection Add** | `recipe_collection_items` | `added_at` | **4.0** | Strong intent (saving for later). |
| **Comment** | `recipe_comments` | `created_at` | **2.0** | Engagement, but can be negative (though usually positive/neutral). |
| **Share** | *N/A* | *N/A* | *5.0* | *Data currently unavailable in DB schema.* |
<!-- markdownlint-enable MD013 -->

> **Note on Shares:** The initial requirements mentioned "shares" data, but
> no explicit `shares` table was found in the provided schema. If share data
> is available (e.g., via a separate analytics event stream), it should be
> included with a high weight.

## Decay Factor ($\lambda$)

We want the "half-life" of an interaction to be appropriate for a recipe app.

* If we want an interaction to lose 50% of its value after **3 days**:
  * $0.5 = e^{-\lambda \times 3}$
  * $\ln(0.5) = -3\lambda$
  * $\lambda \approx 0.23$ (if Age is in days)

## Proposed SQL Implementation (Concept)

To efficiently fetch trending recipes, we can aggregate the scores.

```sql
SELECT
    r.recipe_id,
    r.title,
    (
        -- Weighted Favorites
        COALESCE(
            SUM(
                3.0 * EXP(
                    -0.23 * EXTRACT(EPOCH FROM (NOW() - rf.favorited_at)) / 86400
                )
            ),
            0
        ) +
        -- Weighted Comments
        COALESCE(
            SUM(
                2.0 * EXP(
                    -0.23 * EXTRACT(EPOCH FROM (NOW() - rc.created_at)) / 86400
                )
            ),
            0
        ) +
        -- Weighted Collection Adds
        COALESCE(
            SUM(
                4.0 * EXP(
                    -0.23 * EXTRACT(EPOCH FROM (NOW() - rci.added_at)) / 86400
                )
            ),
            0
        )
    ) as trending_score
FROM
    recipe_manager.recipes r
    LEFT JOIN recipe_manager.recipe_favorites rf ON r.recipe_id = rf.recipe_id
        AND rf.favorited_at > NOW() - INTERVAL '30 days'
    LEFT JOIN recipe_manager.recipe_comments rc ON r.recipe_id = rc.recipe_id
        AND rc.created_at > NOW() - INTERVAL '30 days'
    LEFT JOIN recipe_manager.recipe_collection_items rci ON r.recipe_id = rci.recipe_id
        AND rci.added_at > NOW() - INTERVAL '30 days'
GROUP BY
    r.recipe_id
ORDER BY
    trending_score DESC
LIMIT 50;
```

## Optimization Strategy

Calculating this on the fly for every request is expensive.

1. **Materialized View:** Create a view that refreshes periodically (e.g.,
    every hour).
2. **Cached Standings:** Run a background job to compute the top 100 IDs and
    store them in Redis or a simple lookup table.
