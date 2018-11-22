package com.rewedigital.composer.composing;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

/**
 * Fetches content for some include using a provided fallback in case of an
 * error.
 */
public interface ContentFetcher {

    CompletableFuture<Response<String>> fetch(final String path, final String fallback,
            final Optional<Duration> ttl, final CompositionStep step);

}
