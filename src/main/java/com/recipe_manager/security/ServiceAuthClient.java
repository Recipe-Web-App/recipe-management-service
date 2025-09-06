package com.recipe_manager.security;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.model.enums.ExternalServiceName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client for OAuth2 service-to-service authentication using Client Credentials Flow.
 *
 * <p>This client:
 *
 * <ul>
 *   <li>Manages service-to-service OAuth2 tokens via Client Credentials Flow
 *   <li>Provides automatic token caching and refresh
 *   <li>Used by external service clients for authenticated requests
 *   <li>Integrates with existing Resilience4j patterns
 * </ul>
 */
@Service
public class ServiceAuthClient {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuthClient.class);

  /** Resilience4j service name for OAuth2 service. */
  private static final String OAUTH2_SERVICE = "oauth2-service";

  /** Token buffer time in seconds to refresh before expiry. */
  private static final long TOKEN_BUFFER_SECONDS = 30;

  /** Cached service token with thread-safe access. */
  private final AtomicReference<ServiceToken> cachedServiceToken = new AtomicReference<>();

  /** REST template for HTTP calls. */
  private final RestTemplate restTemplate;

  /** OAuth2 service configuration. */
  private final ExternalServicesConfig.OAuth2ServiceConfig oauth2Config;

  /**
   * Constructs a new ServiceAuthClient.
   *
   * @param restTemplate The REST template for HTTP calls
   * @param externalServicesConfig The external services configuration
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public ServiceAuthClient(
      final RestTemplate restTemplate, final ExternalServicesConfig externalServicesConfig) {
    this.restTemplate = restTemplate;
    this.oauth2Config = externalServicesConfig.getOauth2Service();
  }

  /**
   * Gets a valid service access token for service-to-service authentication.
   *
   * @return CompletableFuture containing the access token
   * @throws ExternalServiceException if token acquisition fails
   */
  @CircuitBreaker(name = OAUTH2_SERVICE)
  @Retry(name = OAUTH2_SERVICE)
  @TimeLimiter(name = OAUTH2_SERVICE)
  @Bulkhead(name = OAUTH2_SERVICE)
  public CompletableFuture<String> getServiceAccessToken() {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            ServiceToken current = cachedServiceToken.get();

            // Check if we have a valid cached token
            if (current != null && !isTokenExpired(current)) {
              LOGGER.debug("Using cached service token");
              return current.getAccessToken();
            }

            LOGGER.debug("Acquiring new service token via client credentials flow");

            // Request new token via client credentials flow
            ServiceToken newToken = requestClientCredentialsToken();
            cachedServiceToken.set(newToken);

            LOGGER.info(
                "Service token acquired successfully, expires in {} seconds",
                newToken.getExpiresIn());

            return newToken.getAccessToken();

          } catch (RestClientException e) {
            LOGGER.error("Failed to acquire service token: {}", e.getMessage());
            throw new ExternalServiceException(
                ExternalServiceName.OAUTH2_SERVICE, "Failed to acquire service token", e);
          }
        });
  }

  /**
   * Requests a new token using OAuth2 Client Credentials Flow.
   *
   * @return ServiceToken containing the access token and metadata
   * @throws RestClientException if the request fails
   */
  private ServiceToken requestClientCredentialsToken() {
    String tokenUrl = oauth2Config.getBaseUrl() + oauth2Config.getTokenPath();

    // Prepare request headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth(oauth2Config.getClientId(), oauth2Config.getClientSecret());

    // Prepare request body for client credentials flow
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client_credentials");

    // Add scopes if configured
    String scopes = oauth2Config.getScopes();
    if (StringUtils.hasText(scopes)) {
      body.add("scope", scopes);
    }

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    LOGGER.debug("Requesting client credentials token from: {}", tokenUrl);

    ResponseEntity<TokenResponse> response =
        restTemplate.postForEntity(tokenUrl, request, TokenResponse.class);

    TokenResponse tokenResponse = response.getBody();
    if (tokenResponse == null) {
      throw new ExternalServiceException(
          ExternalServiceName.OAUTH2_SERVICE, "Empty response from OAuth2 token endpoint");
    }

    return ServiceToken.builder()
        .accessToken(tokenResponse.getAccessToken())
        .tokenType(tokenResponse.getTokenType())
        .expiresIn(tokenResponse.getExpiresIn())
        .scope(tokenResponse.getScope())
        .acquiredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Checks if a service token is expired or close to expiry.
   *
   * @param token The token to check
   * @return true if the token is expired or close to expiry
   */
  private boolean isTokenExpired(final ServiceToken token) {
    if (token == null || token.getAcquiredAt() == null || token.getExpiresIn() == null) {
      return true;
    }

    LocalDateTime expiryTime =
        token.getAcquiredAt().plusSeconds(token.getExpiresIn() - TOKEN_BUFFER_SECONDS);

    return LocalDateTime.now().isAfter(expiryTime);
  }

  /** DTO for OAuth2 token response. */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  static class TokenResponse {
    /** The OAuth2 access token. */
    @JsonProperty("access_token")
    private String accessToken;

    /** The token type (typically "Bearer"). */
    @JsonProperty("token_type")
    private String tokenType;

    /** Token expiration time in seconds. */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /** The scope of the access token. */
    @JsonProperty("scope")
    private String scope;
  }

  /** Internal representation of a cached service token. */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  private static class ServiceToken {
    /** The cached access token. */
    private String accessToken;

    /** The cached token type. */
    private String tokenType;

    /** The cached token expiration time in seconds. */
    private Integer expiresIn;

    /** The cached token scope. */
    private String scope;

    /** When this token was acquired. */
    private LocalDateTime acquiredAt;
  }
}
