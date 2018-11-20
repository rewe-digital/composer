package com.rewedigital.composer.routing;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposerFactory;
import com.rewedigital.composer.response.ComposedResponse;
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
    private final ExtensionAwareRequestClient templateClient;

    public TemplateRoute(final ExtensionAwareRequestClient templateClient, final ComposerFactory composerFactory) {
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
            final Map<String, Object> pathArguments, final ComposedResponse<ByteString> templateResponse,
            final String path) {
        final Response<ByteString> response = templateResponse.response();

        if (isError(response)) {
            // TODO: implement proper error handling
            return CompletableFuture
                    .completedFuture(Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!")));
        }

        return composerFactory
                .build(client, pathArguments, templateResponse.composition())
                .composeTemplate(response.withPayload(response.payload().get().utf8()), path)
                .thenApply(r -> r.composedResponse())
                .thenApply(this::toByteString);
    }

    private Response<ByteString> toByteString(final Response<String> response) {
        return response.withPayload(response.payload().map(ByteString::encodeUtf8).orElse(ByteString.EMPTY));
    }

    private boolean isError(final Response<ByteString> response) {
        return response.status().code() != Status.OK.code() || !response.payload().isPresent();
    }
}
