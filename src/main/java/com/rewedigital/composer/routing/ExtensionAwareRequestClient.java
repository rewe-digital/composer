package com.rewedigital.composer.routing;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.util.response.ExtendableResponse;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class ExtensionAwareRequestClient {

    public CompletionStage<ExtendableResponse<ByteString>> fetch(final RouteMatch rm, final RequestContext context,
            final ResponseComposition extension) {
        return context.requestScopedClient()
                .send(extension.enrich(withTtl(Request.forUri(rm.expandedPath(), context.request().method()), rm.ttl())))
                .thenApply(r -> new ExtendableResponse<>(r, extension.composedWithFragmentFor(r)));
    }

    private Request withTtl(final Request request, final Optional<Duration> ttl) {
        return ttl.map(t -> request.withTtl(t)).orElse(request);
    }

}
