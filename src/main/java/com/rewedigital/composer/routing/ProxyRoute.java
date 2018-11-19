package com.rewedigital.composer.routing;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.util.response.ComposedResponse;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

/**
 * {@link RouteType} <em>proxy</em> just proxies the request to the routing target.
 */
public class ProxyRoute implements RouteType {

    private final ExtensionAwareRequestClient templateClient;

    public ProxyRoute(final ExtensionAwareRequestClient templateClient) {
        this.templateClient = Objects.requireNonNull(templateClient);
    }

    @Override
    public CompletionStage<ComposedResponse<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final ResponseComposition extension) {
        return templateClient.fetch(rm, context, extension);
    }
}
