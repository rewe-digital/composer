package com.rewedigital.composer.routing;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.session.SessionRoot;
import com.rewedigital.composer.util.response.ExtendableResponse;
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
    public CompletionStage<ExtendableResponse<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final SessionRoot session) {
        return templateClient.fetch(rm, context, session);
    }
}
