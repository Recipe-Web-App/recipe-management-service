package com.recipe_manager.service.external;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.recipe_manager.client.recipescraper.RecipeScraperClient;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.exception.ExternalServiceTimeoutException;
import com.recipe_manager.exception.RecipeScraperException;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.enums.ExternalServiceName;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Service wrapper for recipe scraper external service integration. Provides business logic layer
 * with caching, circuit breaker, retry, and error handling for recipe scraper service calls.
 */
@Service
public class RecipeScraperService {

  /** Logger for service operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RecipeScraperService.class);

  /** Name used for circuit breaker configuration. */
  private static final String CIRCUIT_BREAKER_NAME = "recipe-scraper";

  /** Name used for retry configuration. */
  private static final String RETRY_NAME = "recipe-scraper";

  /** Client for recipe scraper service integration. */
  @Autowired private RecipeScraperClient recipeScraperClient;

  /** Configuration for external services. */
  @Autowired private ExternalServicesConfig externalServicesConfig;

  /** Metrics registry for observability. */
  @Autowired private MeterRegistry meterRegistry;

  /** Counter for tracking service calls. */
  private Counter callsCounter;

  /** Counter for tracking service failures. */
  private Counter failuresCounter;

  /** Timer for tracking response times. */
  private Timer responseTimer;

  /** Initializes metrics for monitoring service calls. */
  @jakarta.annotation.PostConstruct
  public void initMetrics() {
    if (meterRegistry != null) {
      callsCounter =
          Counter.builder("external.service.calls")
              .description("Total number of recipe scraper service calls")
              .tag("service", ExternalServiceName.RECIPE_SCRAPER.getServiceName())
              .register(meterRegistry);

      failuresCounter =
          Counter.builder("external.service.failures")
              .description("Total number of recipe scraper service failures")
              .tag("service", ExternalServiceName.RECIPE_SCRAPER.getServiceName())
              .register(meterRegistry);

      responseTimer =
          Timer.builder("external.service.response.time")
              .description("Response time for recipe scraper service calls")
              .tag("service", ExternalServiceName.RECIPE_SCRAPER.getServiceName())
              .register(meterRegistry);
    }
  }

  /**
   * Retrieves shopping information with pricing for a recipe. Includes caching, circuit breaker,
   * and retry logic for resilient external service integration.
   *
   * @param recipeId the recipe ID to get shopping info for
   * @return CompletableFuture with shopping information with pricing details
   * @throws RecipeScraperException if the service call fails after retries
   */
  @Cacheable(
      value = "recipe-scraper-shopping-info",
      key = "#recipeId",
      cacheManager = "externalServicesCacheManager",
      condition = "#recipeId != null")
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getShoppingInfoFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<RecipeScraperShoppingDto> getShoppingInfo(final Long recipeId) {
    return CompletableFuture.supplyAsync(
        () -> {
          // Add correlation ID to MDC for structured logging
          final String correlationId =
              MDC.get("correlationId") != null
                  ? MDC.get("correlationId")
                  : java.util.UUID.randomUUID().toString();
          if (MDC.get("correlationId") == null) {
            MDC.put("correlationId", correlationId);
          }
          MDC.put("externalService", ExternalServiceName.RECIPE_SCRAPER.getServiceName());
          MDC.put("recipeId", recipeId.toString());

          if (!externalServicesConfig.getRecipeScraper().getEnabled()) {
            LOGGER.info(
                "Recipe scraper service is disabled, returning fallback data for recipe {}",
                recipeId);
            return getShoppingInfoFallbackSync(recipeId, new RuntimeException("Service disabled"));
          }

          if (callsCounter != null) {
            callsCounter.increment();
          }

          try {
            if (responseTimer != null) {
              try {
                return responseTimer.recordCallable(
                    () -> callRecipeScraperService(recipeId, correlationId));
              } catch (ExternalServiceTimeoutException e) {
                throw e;
              } catch (Exception e) {
                if (failuresCounter != null) {
                  failuresCounter.increment();
                }
                LOGGER.error(
                    "Failed to retrieve shopping info for recipe {}: {}",
                    recipeId,
                    e.getMessage(),
                    e);
                throw new RecipeScraperException(
                    recipeId, "Failed to retrieve shopping information", e);
              }
            } else {
              return callRecipeScraperService(recipeId, correlationId);
            }
          } finally {
            // Clean up MDC
            MDC.remove("externalService");
            MDC.remove("recipeId");
          }
        });
  }

  /**
   * Fallback method for when recipe scraper service is unavailable. Returns empty pricing data to
   * allow the application to continue functioning without pricing information.
   *
   * @param recipeId the recipe ID
   * @param exception the exception that triggered the fallback
   * @return CompletableFuture with fallback shopping information with no pricing data
   */
  public CompletableFuture<RecipeScraperShoppingDto> getShoppingInfoFallback(
      final Long recipeId, final Exception exception) {
    LOGGER.warn(
        "Using fallback for recipe scraper service for recipe {}: {}",
        recipeId,
        exception.getMessage());

    RecipeScraperShoppingDto fallbackData =
        RecipeScraperShoppingDto.builder()
            .recipeId(recipeId)
            .ingredients(java.util.Collections.emptyMap())
            .totalEstimatedCost(java.math.BigDecimal.ZERO)
            .build();

    return CompletableFuture.completedFuture(fallbackData);
  }

  /**
   * Synchronous fallback method for internal use.
   *
   * @param recipeId the recipe ID
   * @param exception the exception that triggered the fallback
   * @return fallback shopping information with no pricing data
   */
  private RecipeScraperShoppingDto getShoppingInfoFallbackSync(
      final Long recipeId, final Exception exception) {
    LOGGER.warn(
        "Using fallback for recipe scraper service for recipe {}: {}",
        recipeId,
        exception.getMessage());

    return RecipeScraperShoppingDto.builder()
        .recipeId(recipeId)
        .ingredients(java.util.Collections.emptyMap())
        .totalEstimatedCost(java.math.BigDecimal.ZERO)
        .build();
  }

  /**
   * Calls the recipe scraper service with consistent error handling.
   *
   * @param recipeId The recipe ID to get shopping info for
   * @param correlationId The correlation ID for tracing
   * @return The shopping information from the external service
   * @throws RecipeScraperException if the call fails
   * @throws ExternalServiceTimeoutException if the call times out
   */
  private RecipeScraperShoppingDto callRecipeScraperService(
      final Long recipeId, final String correlationId) {
    try {
      LOGGER.info(
          "Calling recipe scraper service for recipe {} with correlation ID {}",
          recipeId,
          correlationId);
      RecipeScraperShoppingDto result = recipeScraperClient.getShoppingInfo(recipeId);
      LOGGER.info(
          "Successfully retrieved shopping info for recipe {} with {} ingredients",
          recipeId,
          result.getIngredients().size());
      return result;
    } catch (ExternalServiceException e) {
      if (failuresCounter != null) {
        failuresCounter.increment();
      }
      LOGGER.error(
          "Failed to retrieve shopping info for recipe {}: {}", recipeId, e.getMessage(), e);
      if (e.getStatusCode() == HttpStatus.REQUEST_TIMEOUT.value()) {
        long timeoutMs = externalServicesConfig.getRecipeScraper().getTimeout().toMillis();
        throw new ExternalServiceTimeoutException(ExternalServiceName.RECIPE_SCRAPER, timeoutMs, e);
      }
      throw new RecipeScraperException(recipeId, "Failed to retrieve shopping information", e);
    } catch (Exception e) {
      if (failuresCounter != null) {
        failuresCounter.increment();
      }
      LOGGER.error(
          "Failed to retrieve shopping info for recipe {}: {}", recipeId, e.getMessage(), e);
      throw new RecipeScraperException(recipeId, "Failed to retrieve shopping information", e);
    }
  }

  /**
   * Checks if the recipe scraper service is currently available based on circuit breaker state.
   *
   * @return true if the service is available, false otherwise
   */
  public boolean isServiceAvailable() {
    return externalServicesConfig.getRecipeScraper().getEnabled();
  }
}
