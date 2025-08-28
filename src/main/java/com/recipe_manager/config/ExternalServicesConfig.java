package com.recipe_manager.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Configuration properties for external services. Provides centralized configuration for all
 * downstream service integrations.
 */
@Configuration
@ConfigurationProperties(prefix = "external.services")
@EnableConfigurationProperties
@Validated
@Data
public class ExternalServicesConfig {

  /** Recipe scraper service configuration. */
  @Valid @NotNull private RecipeScraperConfig recipeScraper = new RecipeScraperConfig();

  /** Media manager service configuration. */
  @Valid @NotNull private MediaManagerConfig mediaManager = new MediaManagerConfig();

  /** Common configuration for all external services. */
  @Valid @NotNull private CommonConfig common = new CommonConfig();

  @Data
  public static class RecipeScraperConfig {
    /** Base URL for the recipe scraper service. */
    @NotBlank private String baseUrl;

    /** API path for shopping info endpoint. */
    @NotBlank private String shoppingInfoPath;

    /** Whether the service is enabled. */
    @NotNull private Boolean enabled;

    /** Request timeout for this service. */
    @NotNull private Duration timeout;

    /** API key for authentication (if required). */
    private String apiKey;
  }

  @Data
  public static class MediaManagerConfig {
    /** Base URL for the media manager service. */
    @NotBlank private String baseUrl;

    /** Whether the service is enabled. */
    @NotNull private Boolean enabled;

    /** Request timeout for this service. */
    @NotNull private Duration timeout;
  }

  @Data
  public static class CommonConfig {
    /** Default connection timeout for external services. */
    @NotNull private Duration connectTimeout;

    /** Default read timeout for external services. */
    @NotNull private Duration readTimeout;

    /** Maximum number of connections per route. */
    @Positive private int maxConnectionsPerRoute;

    /** Maximum total connections. */
    @Positive private int maxTotalConnections;

    /** Whether to enable request/response logging. */
    @NotNull private Boolean logRequests;

    /** Whether to enable distributed tracing. */
    @NotNull private Boolean enableTracing;
  }
}
