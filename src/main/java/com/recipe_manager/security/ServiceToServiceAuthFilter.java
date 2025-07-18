package com.recipe_manager.security;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service-to-service authentication filter for internal service communication.
 *
 * <p>This filter:
 *
 * <ul>
 *   <li>Handles service-to-service authentication using API keys
 *   <li>Validates internal service requests
 *   <li>Sets up service authentication context
 *   <li>Works alongside JWT authentication
 * </ul>
 */
@Component
public final class ServiceToServiceAuthFilter extends OncePerRequestFilter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceToServiceAuthFilter.class);

  /** Service authentication header name. */
  private static final String SERVICE_AUTH_HEADER = "X-Service-Auth";

  /** Service name header name. */
  private static final String SERVICE_NAME_HEADER = "X-Service-Name";

  /** Service authentication key from configuration. */
  @Value("${app.security.service.auth.key:}")
  private String serviceAuthKey;

  /** Flag indicating if service authentication is enabled. */
  @Value("${app.security.service.auth.enabled:false}")
  private boolean serviceAuthEnabled;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    if (!serviceAuthEnabled) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String serviceAuth = request.getHeader(SERVICE_AUTH_HEADER);
      String serviceName = request.getHeader(SERVICE_NAME_HEADER);

      if (StringUtils.hasText(serviceAuth) && serviceAuth.equals(serviceAuthKey)) {
        // Valid service-to-service authentication
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                "service-" + serviceName,
                null,
                Arrays.asList(new SimpleGrantedAuthority("ROLE_SERVICE")));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOGGER.debug(
            "Service authentication successful: {} - RequestId: {}",
            serviceName,
            extractRequestId(request));
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
  private String extractRequestId(final HttpServletRequest request) {
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
