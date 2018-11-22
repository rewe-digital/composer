package com.rewedigital.composer.routing;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposingResponse;
import com.rewedigital.composer.composing.ResponseComposition;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class CompositionAwareRequestClient {

    public CompletionStage<ComposingResponse<ByteString>> fetch(final RouteMatch rm, final RequestContext context,
            final ResponseComposition composition) {
        return context.requestScopedClient()
                .send(composition.enrich(request(rm, context)))
                .thenApply(response -> ComposingResponse.composedFrom(response, composition, rm.expandedPath()));
    }

    private Request request(final RouteMatch rm, final RequestContext context) {
        return withTtl(Request.forUri(rm.expandedPath(), context.request().method()), rm.ttl());
    }

    private Request withTtl(final Request request, final Optional<Duration> ttl) {
        return ttl.map(t -> request.withTtl(t)).orElse(request);
    }
}
