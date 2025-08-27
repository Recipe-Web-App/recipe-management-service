package com.recipe_manager.service.external.mediamanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipe_manager.client.mediamanager.MediaManagerClient;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.model.enums.ExternalServiceName;

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
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "URF_UNREAD_FIELD",
      justification = "Field will be used when endpoint methods are added to MediaManagerClient")
  private Counter callsCounter;

  /** Counter for tracking service failures. */
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "URF_UNREAD_FIELD",
      justification = "Field will be used when endpoint methods are added to MediaManagerClient")
  private Counter failuresCounter;

  /** Timer for tracking response times. */
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "URF_UNREAD_FIELD",
      justification = "Field will be used when endpoint methods are added to MediaManagerClient")
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
   * Checks if the media manager service is currently available based on configuration.
   *
   * @return true if the service is available, false otherwise
   */
  public boolean isServiceAvailable() {
    return externalServicesConfig.getMediaManager().getEnabled();
  }

  // TODO: Add actual service methods as endpoint methods are added to MediaManagerClient
}
