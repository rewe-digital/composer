package com.rewedigital.composer.proxy;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.routing.BackendRouting;
import com.rewedigital.composer.routing.RouteTypes;
import com.rewedigital.composer.util.response.ExtendableResponse;
import com.rewedigital.composer.util.response.ResponseExtension;
import com.rewedigital.composer.util.response.ResponseExtensionHandler;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ComposingRequestHandler {

    private final BackendRouting routing;
    private final RouteTypes routeTypes;
    private final ResponseExtensionHandler extensionHandler;

    public ComposingRequestHandler(final BackendRouting routing, final RouteTypes routeTypes,
        final ResponseExtensionHandler extensionHandler) {
        this.routing = Objects.requireNonNull(routing);
        this.routeTypes = Objects.requireNonNull(routeTypes);
        this.extensionHandler = Objects.requireNonNull(extensionHandler);

    }

    public CompletionStage<Response<ByteString>> execute(final RequestContext context) {
        return extensionHandler.initialize(context).thenCompose(extensions -> {
            return routing.matches(context.request())
                .map(rm -> rm.routeType(routeTypes)
                    .execute(rm, context, extensions))
                .orElse(defaultResponse(extensions))
                .thenApply(response -> response.writeSessionToResponse());
        });
    }

    private static CompletableFuture<ExtendableResponse<ByteString>> defaultResponse(
        final ResponseExtension extensions) {
        final Response<ByteString> response =
            Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!"));
        return CompletableFuture
            .completedFuture(new ExtendableResponse<ByteString>(response, extensions));
    }
}
