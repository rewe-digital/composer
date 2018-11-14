package com.rewedigital.composer.routing;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.session.SessionRoot;
import com.rewedigital.composer.util.response.ExtendableResponse;
import com.rewedigital.composer.util.response.Extension;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class ExtensionAwareRequestClient {

    public CompletionStage<ExtendableResponse<ByteString>> fetch(final RouteMatch rm, final RequestContext context,
            final SessionRoot session) {
        final Extension extension = Extension.of(asList(session));
        return context.requestScopedClient()
                .send(session.enrich(withTtl(Request.forUri(rm.expandedPath(), context.request().method()), rm.ttl())))
                .thenApply(r -> new ExtendableResponse<>(r, extension.mergedWithFragmentFor(r)));
    }

    private Request withTtl(final Request request, final Optional<Duration> ttl) {
        return ttl.map(t -> request.withTtl(t)).orElse(request);
    }

}
