package com.recipe_manager.service.external.mediamanager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.client.mediamanager.MediaManagerClient;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.exception.ExternalServiceTimeoutException;
import com.recipe_manager.exception.MediaManagerException;
import com.recipe_manager.model.dto.external.mediamanager.health.HealthResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.health.ReadinessResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.media.MediaDto;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;
import com.recipe_manager.model.enums.ExternalServiceName;
import com.recipe_manager.model.enums.HealthStatus;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.enums.ReadinessStatus;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Service wrapper for media manager external service integration. Provides business logic layer
 * with caching, circuit breaker, retry, and error handling for media manager service calls.
 */
@Service
public class MediaManagerService {

  /** Logger for service operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaManagerService.class);

  /** Name used for circuit breaker configuration. */
  private static final String CIRCUIT_BREAKER_NAME = "media-manager";

  /** Name used for retry configuration. */
  private static final String RETRY_NAME = "media-manager";

  /** Client for media manager service integration. */
  @Autowired private MediaManagerClient mediaManagerClient;

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
              .description("Total number of media manager service calls")
              .tag("service", ExternalServiceName.MEDIA_SERVICE.getServiceName())
              .register(meterRegistry);

      failuresCounter =
          Counter.builder("external.service.failures")
              .description("Total number of media manager service failures")
              .tag("service", ExternalServiceName.MEDIA_SERVICE.getServiceName())
              .register(meterRegistry);

      responseTimer =
          Timer.builder("external.service.response.time")
              .description("Response time for media manager service calls")
              .tag("service", ExternalServiceName.MEDIA_SERVICE.getServiceName())
              .register(meterRegistry);
    }
  }

  /**
   * Retrieves health status of the media manager service.
   *
   * @return CompletableFuture with health information
   */
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getHealthFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<HealthResponseDto> getHealth() {
    return CompletableFuture.supplyAsync(
        () -> {
          setupMDC("getHealth");

          if (!externalServicesConfig.getMediaManager().getEnabled()) {
            LOGGER.info("Media manager service is disabled, returning fallback health status");
            return getHealthFallbackSync(new RuntimeException("Service disabled"));
          }

          incrementCallsCounter();

          try {
            return executeWithTimer(
                () -> {
                  LOGGER.info("Calling media manager health endpoint");
                  HealthResponseDto result = mediaManagerClient.getHealth();
                  LOGGER.info("Successfully retrieved health status: {}", result.getStatus());
                  return result;
                });
          } catch (Exception e) {
            return handleException(e, "health check", null);
          } finally {
            cleanupMDC();
          }
        });
  }

  /**
   * Retrieves readiness status of the media manager service.
   *
   * @return CompletableFuture with readiness information
   */
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getReadinessFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<ReadinessResponseDto> getReadiness() {
    return CompletableFuture.supplyAsync(
        () -> {
          setupMDC("getReadiness");

          if (!externalServicesConfig.getMediaManager().getEnabled()) {
            LOGGER.info("Media manager service is disabled, returning fallback readiness status");
            return getReadinessFallbackSync(new RuntimeException("Service disabled"));
          }

          incrementCallsCounter();

          try {
            return executeWithTimer(
                () -> {
                  LOGGER.info("Calling media manager readiness endpoint");
                  ReadinessResponseDto result = mediaManagerClient.getReadiness();
                  LOGGER.info("Successfully retrieved readiness status: {}", result.getStatus());
                  return result;
                });
          } catch (Exception e) {
            return handleException(e, "readiness check", null);
          } finally {
            cleanupMDC();
          }
        });
  }

  /**
   * Uploads a media file to the media manager service.
   *
   * @param file the file to upload
   * @return CompletableFuture with upload response
   */
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "uploadMediaFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<UploadMediaResponseDto> uploadMedia(final MultipartFile file) {
    return CompletableFuture.supplyAsync(
        () -> {
          setupMDC("uploadMedia");
          MDC.put("filename", file.getOriginalFilename());

          if (!externalServicesConfig.getMediaManager().getEnabled()) {
            LOGGER.info("Media manager service is disabled, returning fallback upload response");
            return uploadMediaFallbackSync(file, new RuntimeException("Service disabled"));
          }

          incrementCallsCounter();

          try {
            return executeWithTimer(
                () -> {
                  LOGGER.info(
                      "Uploading media file: {} (size: {} bytes)",
                      file.getOriginalFilename(),
                      file.getSize());
                  UploadMediaResponseDto result = mediaManagerClient.uploadMedia(file);
                  LOGGER.info("Successfully uploaded media file: media_id={}", result.getMediaId());
                  return result;
                });
          } catch (Exception e) {
            return handleException(e, "media upload", null);
          } finally {
            cleanupMDC();
            MDC.remove("filename");
          }
        });
  }

  /**
   * Retrieves a list of media files with optional filtering and pagination.
   *
   * @param limit maximum number of items to return (optional)
   * @param offset number of items to skip for pagination (optional)
   * @param status filter by processing status (optional)
   * @return CompletableFuture with list of media information
   */
  @Cacheable(
      value = "media-manager-list",
      key = "#limit + '_' + #offset + '_' + #status",
      cacheManager = "externalServicesCacheManager",
      condition = "#limit != null && #offset != null")
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "listMediaFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<List<MediaDto>> listMedia(
      final Integer limit, final Integer offset, final String status) {
    return CompletableFuture.supplyAsync(
        () -> {
          setupMDC("listMedia");
          MDC.put("limit", String.valueOf(limit));
          MDC.put("offset", String.valueOf(offset));
          MDC.put("status", status);

          if (!externalServicesConfig.getMediaManager().getEnabled()) {
            LOGGER.info("Media manager service is disabled, returning empty list");
            return listMediaFallbackSync(
                limit, offset, status, new RuntimeException("Service disabled"));
          }

          incrementCallsCounter();

          try {
            return executeWithTimer(
                () -> {
                  LOGGER.info(
                      "Listing media files with limit={}, offset={}, status={}",
                      limit,
                      offset,
                      status);
                  List<MediaDto> result = mediaManagerClient.listMedia(limit, offset, status);
                  LOGGER.info("Successfully retrieved {} media items", result.size());
                  return result;
                });
          } catch (Exception e) {
            return handleException(e, "media list", null);
          } finally {
            cleanupMDC();
            MDC.remove("limit");
            MDC.remove("offset");
            MDC.remove("status");
          }
        });
  }

  /**
   * Permanently delete a media file from the external media manager service.
   *
   * @param mediaId the unique identifier of the media file to delete
   * @return CompletableFuture that completes when the deletion is finished
   * @throws ExternalServiceException if the service is unavailable or returns an error
   * @throws ExternalServiceTimeoutException if the request times out
   * @throws MediaManagerException if there is an error processing the deletion request
   */
  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteMediaFallback")
  @Retry(name = RETRY_NAME)
  @TimeLimiter(name = CIRCUIT_BREAKER_NAME)
  public CompletableFuture<Void> deleteMedia(final Long mediaId) {
    return CompletableFuture.supplyAsync(
        () -> {
          setupMDC("deleteMedia");
          MDC.put("mediaId", String.valueOf(mediaId));

          if (!externalServicesConfig.getMediaManager().getEnabled()) {
            LOGGER.info(
                "Media manager service is disabled, skipping media deletion for ID: {}", mediaId);
            return null;
          }

          incrementCallsCounter();

          try {
            return executeWithTimer(
                () -> {
                  LOGGER.info("Deleting media file with ID: {}", mediaId);
                  mediaManagerClient.deleteMedia(mediaId);
                  LOGGER.info("Successfully deleted media file with ID: {}", mediaId);
                  return null;
                });
          } catch (Exception e) {
            return handleException(e, "media deletion", null);
          } finally {
            cleanupMDC();
            MDC.remove("mediaId");
          }
        });
  }

  /**
   * Checks if the media manager service is currently available based on configuration.
   *
   * @return true if the service is available, false otherwise
   */
  public boolean isServiceAvailable() {
    return externalServicesConfig.getMediaManager().getEnabled();
  }

  // Helper methods
  private void setupMDC(final String operation) {
    final String correlationId =
        MDC.get("correlationId") != null
            ? MDC.get("correlationId")
            : java.util.UUID.randomUUID().toString();
    if (MDC.get("correlationId") == null) {
      MDC.put("correlationId", correlationId);
    }
    MDC.put("externalService", ExternalServiceName.MEDIA_SERVICE.getServiceName());
    MDC.put("operation", operation);
  }

  private void cleanupMDC() {
    MDC.remove("externalService");
    MDC.remove("operation");
  }

  private void incrementCallsCounter() {
    if (callsCounter != null) {
      callsCounter.increment();
    }
  }

  private void incrementFailuresCounter() {
    if (failuresCounter != null) {
      failuresCounter.increment();
    }
  }

  private <T> T executeWithTimer(final java.util.concurrent.Callable<T> callable) throws Exception {
    if (responseTimer != null) {
      return responseTimer.recordCallable(callable);
    } else {
      return callable.call();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T handleException(final Exception e, final String operation, final Long mediaId) {
    incrementFailuresCounter();
    LOGGER.error("Failed to execute media manager {} operation: {}", operation, e.getMessage(), e);

    if (e instanceof ExternalServiceTimeoutException) {
      throw (ExternalServiceTimeoutException) e;
    }

    if (e instanceof ExternalServiceException externalServiceException) {
      if (externalServiceException.getStatusCode() == HttpStatus.REQUEST_TIMEOUT.value()) {
        long timeoutMs = externalServicesConfig.getMediaManager().getTimeout().toMillis();
        throw new ExternalServiceTimeoutException(ExternalServiceName.MEDIA_SERVICE, timeoutMs, e);
      }
      if (mediaId != null) {
        throw new MediaManagerException(mediaId, "Failed to execute " + operation, e);
      } else {
        throw new MediaManagerException("Failed to execute " + operation, e);
      }
    }

    if (mediaId != null) {
      throw new MediaManagerException(mediaId, "Failed to execute " + operation, e);
    } else {
      throw new MediaManagerException("Failed to execute " + operation, e);
    }
  }

  // Fallback method implementations
  private HealthResponseDto getHealthFallbackSync(final Exception exception) {
    LOGGER.warn("Using fallback for media manager health check: {}", exception.getMessage());

    return HealthResponseDto.builder()
        .status(HealthStatus.DEGRADED)
        .service("media-management-service")
        .version("unknown")
        .timestamp(java.time.LocalDateTime.now())
        .responseTimeMs(0)
        .checks(HealthResponseDto.HealthChecksDto.builder().overall(HealthStatus.DEGRADED).build())
        .build();
  }

  private ReadinessResponseDto getReadinessFallbackSync(final Exception exception) {
    LOGGER.warn("Using fallback for media manager readiness check: {}", exception.getMessage());

    return ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .service("media-management-service")
        .version("unknown")
        .timestamp(java.time.LocalDateTime.now())
        .responseTimeMs(0)
        .checks(
            ReadinessResponseDto.ReadinessChecksDto.builder()
                .overall(ReadinessStatus.NOT_READY)
                .build())
        .build();
  }

  private UploadMediaResponseDto uploadMediaFallbackSync(
      final MultipartFile file, final Exception exception) {
    LOGGER.warn(
        "Using fallback for media upload of file {}: {}",
        file.getOriginalFilename(),
        exception.getMessage());

    return UploadMediaResponseDto.builder()
        .mediaId(-1L)
        .contentHash("fallback")
        .processingStatus(ProcessingStatus.FAILED)
        .uploadUrl(null)
        .build();
  }

  private List<MediaDto> listMediaFallbackSync(
      final Integer limit, final Integer offset, final String status, final Exception exception) {
    LOGGER.warn(
        "Using fallback for media list (limit={}, offset={}, status={}): {}",
        limit,
        offset,
        status,
        exception.getMessage());
    return java.util.Collections.emptyList();
  }

  // Public fallback methods for Circuit Breaker
  public CompletableFuture<HealthResponseDto> getHealthFallback(final Exception exception) {
    return CompletableFuture.completedFuture(getHealthFallbackSync(exception));
  }

  public CompletableFuture<ReadinessResponseDto> getReadinessFallback(final Exception exception) {
    return CompletableFuture.completedFuture(getReadinessFallbackSync(exception));
  }

  public CompletableFuture<UploadMediaResponseDto> uploadMediaFallback(
      final MultipartFile file, final Exception exception) {
    return CompletableFuture.completedFuture(uploadMediaFallbackSync(file, exception));
  }

  public CompletableFuture<List<MediaDto>> listMediaFallback(
      final Integer limit, final Integer offset, final String status, final Exception exception) {
    return CompletableFuture.completedFuture(
        listMediaFallbackSync(limit, offset, status, exception));
  }

  public CompletableFuture<Void> deleteMediaFallback(
      final Long mediaId, final Exception exception) {
    LOGGER.error(
        "Media manager service is unavailable for media deletion with ID: {}", mediaId, exception);
    return CompletableFuture.completedFuture(null);
  }
}
