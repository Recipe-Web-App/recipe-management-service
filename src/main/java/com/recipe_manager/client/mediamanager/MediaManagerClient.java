package com.recipe_manager.client.mediamanager;

import org.springframework.cloud.openfeign.FeignClient;

import com.recipe_manager.client.common.FeignClientConfig;

/**
 * Feign client for media management service. Provides declarative HTTP client interface for
 * interacting with the external media management service.
 */
@FeignClient(
    name = "media-manager",
    url = "${external.services.media-manager.base-url}",
    configuration = FeignClientConfig.class,
    fallback = com.recipe_manager.service.external.mediamanager.MediaManagerFallback.class)
public interface MediaManagerClient {

  // TODO: Add endpoint methods as needed
}
