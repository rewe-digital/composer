package com.rewedigital.composer.routing;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposerFactory;
import com.rewedigital.composer.response.ComposingResponse;
import com.rewedigital.composer.response.ResponseComposition;
import com.spotify.apollo.Client;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

/**
 * {@link RouteType} <em>template</em> executes composition logic on the
 * response of the route target.
 */
public class TemplateRoute implements RouteType {

    private final ComposerFactory composerFactory;
    private final CompositionAwareRequestClient templateClient;

    public TemplateRoute(final CompositionAwareRequestClient templateClient, final ComposerFactory composerFactory) {
        this.templateClient = Objects.requireNonNull(templateClient);
        this.composerFactory = Objects.requireNonNull(composerFactory);
    }

    @Override
    public CompletionStage<Response<ByteString>> execute(final RouteMatch rm, final RequestContext context,
            final ResponseComposition extensions) {
        return templateClient.fetch(rm, context, extensions)
                .thenCompose(
                        templateResponse -> process(context.requestScopedClient(), rm.parsedPathArguments(),
                                templateResponse,
                                rm.expandedPath()));
    }

    private CompletionStage<Response<ByteString>> process(final Client client,
            final Map<String, Object> pathArguments, final ComposingResponse<ByteString> templateResponse,
            final String path) {

        return templateResponse.toComposablePayload()
                .map(template -> composerFactory.build(client, path, pathArguments, template)
                        .composeTemplate()
                        .thenApply(r -> r.composedResponse())
                        // TODO compose cache-control header
                        .thenApply(r -> r.withHeader("Cache-Control", "no-store,max-age=0"))
                        .thenApply(this::toByteString))
                .orElseGet(() -> CompletableFuture.completedFuture(
                        // TODO proper error handling
                        Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!"))));
    }

    private Response<ByteString> toByteString(final Response<String> response) {
        return response.withPayload(response.payload().map(ByteString::encodeUtf8).orElse(ByteString.EMPTY));
    }
}
