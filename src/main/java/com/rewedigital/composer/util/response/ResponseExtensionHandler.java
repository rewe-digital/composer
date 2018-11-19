package com.rewedigital.composer.util.response;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.RequestContext;

/**
 * Initializes a structure of a {@link ResponseComposition} for a
 * {@link RequestContext}.
 */
public interface ResponseExtensionHandler {

    public CompletionStage<ResponseComposition> initialize(final RequestContext context);

}
