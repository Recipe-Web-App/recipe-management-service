package com.recipe_manager.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.Cache;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.model.enums.ExternalServiceName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client service for OAuth2 authentication service integration.
 *
 * <p>This service handles:
 *
 * <ul>
 *   <li>Service-to-service authentication using client credentials flow
 *   <li>Token introspection for JWT validation
 *   <li>User information retrieval
 *   <li>Access token caching and refresh
 * </ul>
 */
@Service
public class OAuth2Client {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Client.class);

  /** Circuit breaker and retry name for resilience configuration. */
  private static final String OAUTH2_SERVICE = "oauth2-service";

  /** Token buffer time in seconds for expiration checking. */
  private static final long TOKEN_BUFFER_SECONDS = 30;

  /** OAuth2 service configuration. */
  private final ExternalServicesConfig.OAuth2ServiceConfig config;

  /** REST template for HTTP operations. */
  private final RestTemplate restTemplate;

  /** Cache for token introspection responses. */
  private final Cache<String, TokenIntrospectionResponse> tokenIntrospectionCache;

  /** Cached service access token (thread-safe). */
  private final AtomicReference<ServiceToken> cachedServiceToken = new AtomicReference<>();

  /**
   * Constructs a new OAuth2Client.
   *
   * @param externalServicesConfig the external services configuration
   * @param restTemplate the REST template for HTTP operations (shared Spring bean - intentionally
   *     stored as reference)
   * @param tokenIntrospectionCache cache for token introspection responses
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public OAuth2Client(
      final ExternalServicesConfig externalServicesConfig,
      final RestTemplate restTemplate,
      @Qualifier("tokenIntrospectionCache")
          final Cache<String, TokenIntrospectionResponse> tokenIntrospectionCache) {
    this.config = externalServicesConfig.getOauth2Service();
    // RestTemplate is a shared Spring bean designed to be reused
    // Storing the reference is intentional and safe
    this.restTemplate = restTemplate;
    // Caffeine Cache is thread-safe and designed to be shared
    this.tokenIntrospectionCache = tokenIntrospectionCache;
  }

  /**
   * Gets a valid service access token using client credentials flow.
   *
   * @return valid access token
   * @throws ExternalServiceException if token retrieval fails
   */
  @CircuitBreaker(name = OAUTH2_SERVICE)
  @Retry(name = OAUTH2_SERVICE)
  @TimeLimiter(name = OAUTH2_SERVICE)
  public CompletableFuture<String> getServiceAccessToken() {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            // Check if cached token is still valid
            ServiceToken token = cachedServiceToken.get();
            if (token != null && isTokenValid(token)) {
              LOGGER.debug("Using cached service access token");
              return token.getAccessToken();
            }

            // Request new token using client credentials flow
            LOGGER.debug("Requesting new service access token");
            ServiceToken newToken = requestServiceToken();
            cachedServiceToken.set(newToken);

            return newToken.getAccessToken();
          } catch (Exception e) {
            LOGGER.error("Failed to get service access token: {}", e.getMessage());
            throw new ExternalServiceException(
                ExternalServiceName.OAUTH2_SERVICE, "OAuth2 service token request failed", e);
          }
        });
  }

  /**
   * Introspects a token to validate it and get token information.
   *
   * <p>Results are cached with a sliding window TTL that refreshes on access, capped by the token's
   * actual expiration time. Inactive tokens are cached briefly to prevent abuse.
   *
   * @param token the token to introspect
   * @return token introspection response
   * @throws ExternalServiceException if introspection fails
   */
  @CircuitBreaker(name = OAUTH2_SERVICE)
  @Retry(name = OAUTH2_SERVICE)
  @TimeLimiter(name = OAUTH2_SERVICE)
  public CompletableFuture<TokenIntrospectionResponse> introspectToken(final String token) {
    return CompletableFuture.supplyAsync(
        () -> {
          // Check cache first
          TokenIntrospectionResponse cached = tokenIntrospectionCache.getIfPresent(token);
          if (cached != null) {
            LOGGER.debug("Token introspection cache hit");
            return cached;
          }

          try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(config.getClientId(), config.getClientSecret());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("token", token);
            body.add("token_type_hint", "access_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            String url = config.getBaseUrl() + config.getIntrospectionPath();
            ResponseEntity<TokenIntrospectionResponse> response =
                restTemplate.postForEntity(url, request, TokenIntrospectionResponse.class);

            if (response.getBody() == null) {
              throw new ExternalServiceException(
                  ExternalServiceName.OAUTH2_SERVICE, "Empty response from token introspection");
            }

            // Cache the response (expiry handled by custom Expiry policy in CacheConfig)
            tokenIntrospectionCache.put(token, response.getBody());
            LOGGER.debug("Token introspection completed successfully, result cached");
            return response.getBody();

          } catch (RestClientException e) {
            LOGGER.error("Failed to introspect token: {}", e.getMessage());
            throw new ExternalServiceException(
                ExternalServiceName.OAUTH2_SERVICE, "OAuth2 token introspection failed", e);
          }
        });
  }

  /**
   * Gets user information from the OAuth2 service using an access token.
   *
   * @param accessToken the user's access token
   * @return user information
   * @throws ExternalServiceException if user info retrieval fails
   */
  @CircuitBreaker(name = OAUTH2_SERVICE)
  @Retry(name = OAUTH2_SERVICE)
  @TimeLimiter(name = OAUTH2_SERVICE)
  public CompletableFuture<UserInfoResponse> getUserInfo(final String accessToken) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            String url = config.getBaseUrl() + config.getUserInfoPath();
            ResponseEntity<UserInfoResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, request, UserInfoResponse.class);

            if (response.getBody() == null) {
              throw new ExternalServiceException(
                  ExternalServiceName.OAUTH2_SERVICE, "Empty response from user info endpoint");
            }

            LOGGER.debug("User info retrieved successfully");
            return response.getBody();

          } catch (RestClientException e) {
            LOGGER.error("Failed to get user info: {}", e.getMessage());
            throw new ExternalServiceException(
                ExternalServiceName.OAUTH2_SERVICE, "OAuth2 user info retrieval failed", e);
          }
        });
  }

  /**
   * Requests a new service token using client credentials flow.
   *
   * @return new service token
   * @throws ExternalServiceException if token request fails
   */
  private ServiceToken requestServiceToken() {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setBasicAuth(config.getClientId(), config.getClientSecret());

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("grant_type", "client_credentials");
      if (config.getScopes() != null && !config.getScopes().trim().isEmpty()) {
        body.add("scope", config.getScopes());
      }

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

      String url = config.getBaseUrl() + config.getTokenPath();
      ResponseEntity<TokenResponse> response =
          restTemplate.postForEntity(url, request, TokenResponse.class);

      TokenResponse tokenResponse = response.getBody();
      if (tokenResponse == null) {
        throw new ExternalServiceException(
            ExternalServiceName.OAUTH2_SERVICE, "Empty response from token endpoint");
      }

      return new ServiceToken(
          tokenResponse.getAccessToken(), Instant.now().plusSeconds(tokenResponse.getExpiresIn()));

    } catch (RestClientException e) {
      throw new ExternalServiceException(
          ExternalServiceName.OAUTH2_SERVICE, "Failed to request service token", e);
    }
  }

  /**
   * Checks if a service token is still valid (not expired with 30s buffer).
   *
   * @param token the service token to check
   * @return true if valid, false otherwise
   */
  private boolean isTokenValid(final ServiceToken token) {
    return token != null
        && token.getExpiresAt() != null
        && token
            .getExpiresAt()
            .isAfter(Instant.now().plus(Duration.ofSeconds(TOKEN_BUFFER_SECONDS)));
  }

  /** Data class for OAuth2 token response. */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TokenResponse {
    /** The access token. */
    @JsonProperty("access_token")
    private String accessToken;

    /** The token type (usually "Bearer"). */
    @JsonProperty("token_type")
    private String tokenType;

    /** Token expiration time in seconds. */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /** The refresh token for getting new access tokens. */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /** Space-delimited list of granted scopes. */
    private String scope;

    /** OpenID Connect ID token. */
    @JsonProperty("id_token")
    private String idToken;
  }

  /** Data class for token introspection response. */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TokenIntrospectionResponse {
    /** Whether the token is currently active. */
    private Boolean active;

    /** The client identifier for which the token was issued. */
    @JsonProperty("client_id")
    private String clientId;

    /** The username of the resource owner. */
    private String username;

    /** Space-delimited list of scopes. */
    private String scope;

    /** The token type (usually "Bearer"). */
    @JsonProperty("token_type")
    private String tokenType;

    /** Token expiration timestamp (Unix timestamp). */
    private Long exp;

    /** Token issued at timestamp (Unix timestamp). */
    private Long iat;

    /** Subject identifier (user identifier). */
    private String sub;

    /** Array of intended audiences for the token. */
    private String[] aud;

    /** Token issuer identifier. */
    private String iss;

    /** User identifier from the token. */
    @JsonProperty("user_id")
    private String userId;

    /** Array of granted scopes. */
    private String[] scopes;
  }

  /** Data class for user info response. */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInfoResponse {
    /** Subject identifier (unique user identifier). */
    private String sub;

    /** User's full name. */
    private String name;

    /** User's given name (first name). */
    @JsonProperty("given_name")
    private String givenName;

    /** User's family name (last name). */
    @JsonProperty("family_name")
    private String familyName;

    /** User's email address. */
    private String email;

    /** Whether the user's email address has been verified. */
    @JsonProperty("email_verified")
    private Boolean emailVerified;

    /** URL to user's profile picture. */
    private String picture;
  }

  /** Internal class for cached service tokens. */
  @Data
  @Builder
  @AllArgsConstructor
  private static class ServiceToken {
    /** The cached access token. */
    private final String accessToken;

    /** When the token expires. */
    private final Instant expiresAt;
  }
}
