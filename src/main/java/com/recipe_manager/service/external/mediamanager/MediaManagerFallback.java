package com.recipe_manager.service.external.mediamanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.recipe_manager.client.mediamanager.MediaManagerClient;

/**
 * Fallback implementation for media manager service. Provides graceful degradation when the media
 * manager service is unavailable, returning appropriate fallback responses instead of failing the
 * request. This implementation is co-located with MediaManagerService for better code organization
 * and maintenance of related functionality.
 */
@Component
public final class MediaManagerFallback implements MediaManagerClient {

  /** Logger for fallback operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaManagerFallback.class);

  // TODO: Add fallback method implementations as endpoint methods are added to MediaManagerClient
}
