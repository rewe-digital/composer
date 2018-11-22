package com.rewedigital.composer.routing;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ResponseComposition;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

/**
 * {@link RouteType} <em>proxy</em> just proxies the request to the routing
 * target.
 */
public class ProxyRoute implements RouteType {

    private final CompositionAwareRequestClient templateClient;

    public ProxyRoute(final CompositionAwareRequestClient templateClient) {
        this.templateClient = Objects.requireNonNull(templateClient);
    }

    @Override
    public CompletionStage<Response<ByteString>> execute(final RouteMatch rm, final RequestContext context,
            final ResponseComposition composition) {
        return templateClient.fetch(rm, context, composition).thenApply(r -> r.composedResponse());
    }
}
