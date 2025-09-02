package com.recipe_manager.security;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.recipe_manager.config.ExternalServicesConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service authentication filter for OAuth2-based service-to-service communication.
 *
 * <p>This filter:
 *
 * <ul>
 *   <li>Handles service-to-service authentication using OAuth2 Bearer tokens
 *   <li>Validates JWT tokens issued via Client Credentials Flow
 *   <li>Sets up service authentication context with ROLE_SERVICE
 *   <li>Works alongside JWT authentication for user requests
 * </ul>
 */
@Component
public final class ServiceAuthenticationFilter extends OncePerRequestFilter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);

  /** Authorization header name. */
  private static final String AUTHORIZATION_HEADER = "Authorization";

  /** Bearer token prefix. */
  private static final String BEARER_PREFIX = "Bearer ";

  /** Service name header name. */
  private static final String SERVICE_NAME_HEADER = "X-Service-Name";

  /** JWT service for token validation. */
  private final JwtService jwtService;

  /** OAuth2 service configuration. */
  private final ExternalServicesConfig.OAuth2ServiceConfig oauth2Config;

  /**
   * Constructs a new ServiceAuthenticationFilter.
   *
   * @param jwtService The JWT service for token validation
   * @param externalServicesConfig The external services configuration
   */
  public ServiceAuthenticationFilter(
      final JwtService jwtService, final ExternalServicesConfig externalServicesConfig) {
    this.jwtService = jwtService;
    this.oauth2Config = externalServicesConfig.getOauth2Service();
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    // Skip if OAuth2 service or service-to-service authentication is disabled
    if (!oauth2Config.getEnabled() || !oauth2Config.getServiceToServiceEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String authHeader = request.getHeader(AUTHORIZATION_HEADER);

      if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate the JWT token
        if (jwtService.isTokenValid(token)) {
          String clientId = jwtService.extractClientId(token);
          String tokenType = jwtService.extractTokenType(token);

          // Check if this is a service token (client_credentials flow)
          if ("access_token".equals(tokenType) && StringUtils.hasText(clientId)) {
            String serviceName = request.getHeader(SERVICE_NAME_HEADER);

            // Create service authentication
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    "service-" + (StringUtils.hasText(serviceName) ? serviceName : clientId),
                    null,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_SERVICE")));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LOGGER.debug(
                "Service authentication successful: {} (client: {}) - RequestId: {}",
                serviceName != null ? serviceName : clientId,
                clientId,
                extractRequestId(request));
          }
        }
      }
    } catch (Exception e) {
      LOGGER.warn(
          "Service authentication failed: {} - RequestId: {}",
          e.getMessage(),
          extractRequestId(request));
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts request ID from headers for logging purposes.
   *
   * @param request The HTTP request
   * @return The request ID or "unknown" if not found
   */
  String extractRequestId(final HttpServletRequest request) {
    String requestId = request.getHeader("X-Request-ID");
    return requestId != null ? requestId : "unknown";
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/actuator")
        || path.startsWith("/health")
        || path.startsWith("/info")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/api/v1/auth");
  }
}
