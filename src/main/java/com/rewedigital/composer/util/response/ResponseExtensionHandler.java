package com.rewedigital.composer.util.response;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.RequestContext;

/**
 * Initializes a structure of a {@link ResponseExtension} for a
 * {@link RequestContext}.
 */
public interface ResponseExtensionHandler {

    public CompletionStage<ResponseExtension> initialize(final RequestContext context);

}
