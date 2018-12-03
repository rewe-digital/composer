package com.rewedigital.composer.html;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.composer.composing.ComposingResponse;
import com.rewedigital.composer.composing.CompositionStep;
import com.rewedigital.composer.composing.ContentFetcher;
import com.rewedigital.composer.composing.IncludedFragment;
import com.rewedigital.composer.composing.ResponseCompositionFragment;
import com.spotify.apollo.Response;

/**
 * Describes the include parsed from a template. It contains the start and end
 * offsets of the include element in the template for further processing.
 *
 * An included service can {@link #fetch(ContentFetcher, CompositionStep)} the
 * content using a {@link ContentFetcher} creating an instance of
 * {@link HttpFragment.WithResponse} that holds the response.
 */
class HttpFragment implements IncludedFragment {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFragment.class);

    public static class Builder {
        private int startOffset;
        private int endOffset;
        private final Map<String, String> attributes = new HashMap<>();
        private String fallback;

        public Builder startOffset(final int startOffset) {
            this.startOffset = startOffset;
            return this;
        }

        public Builder endOffset(final int endOffset) {
            this.endOffset = endOffset;
            return this;
        }

        public Builder attribute(final String name, final String value) {
            attributes.put(name, value);
            return this;
        }

        public Builder fallback(final String fallback) {
            this.fallback = fallback;
            return this;
        }

        public HttpFragment build() {
            return new HttpFragment(this);
        }
    }

    public static class WithResponse implements FragmentResponse {
        private final Response<String> response;
        private final CompositionStep step;

        private WithResponse(final CompositionStep step, final Response<String> response) {
            this.response = response;
            this.step = step;

            LOGGER.debug("included service response: {} received via {}", response, step);
        }

        @Override
        public CompletableFuture<ResponseCompositionFragment> compose(final Composer composer,
                final ComposingResponse<String> parent) {
            return composer.compose(parent.withResponse(response), step);
        }
    }

    private final int startOffset;
    private final int endOffset;
    private final Map<String, String> attributes;
    private final String fallback;

    private HttpFragment(final Builder builder) {
        this.startOffset = builder.startOffset;
        this.endOffset = builder.endOffset;
        this.attributes = new HashMap<>(builder.attributes);
        this.fallback = builder.fallback;
    }

    @Override
    public CompletableFuture<IncludedFragment.FragmentResponse> fetch(final ContentFetcher fetcher,
            final CompositionStep parentStep) {
        final CompositionStep step = parentStep.childWith(path(), startOffset, endOffset);
        return fetcher.fetch(path(), fallback(), ttl(), step)
                .thenApply(r -> new WithResponse(step, r));
    }

    public boolean isInRage(final ContentRange contentRange) {
        return contentRange.isInRange(startOffset);
    }

    private String fallback() {
        return fallback;
    }

    private String path() {
        return attributes.getOrDefault("path", "");
    }

    private Optional<Duration> ttl() {
        return longAttribute("ttl").map(Duration::ofMillis);
    }

    private Optional<Long> longAttribute(final String name) {
        return Optional.ofNullable(attributes.get(name)).flatMap(v -> {
            try {
                return Optional.of(Long.parseLong(v));
            } catch (final NumberFormatException nfEx) {
                LOGGER.info("Not able to evaluate {} for path {} with value {}.", name, path(), v);
                return Optional.empty();
            }
        });
    }
}
