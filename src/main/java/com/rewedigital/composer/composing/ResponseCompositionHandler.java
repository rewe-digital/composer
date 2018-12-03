package com.rewedigital.composer.composing;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.RequestContext;

/**
 * Initializes a structure of a {@link ResponseComposition} for a
 * {@link RequestContext}.
 */
public interface ResponseCompositionHandler {

    public CompletionStage<ResponseComposition> initializeFrom(final RequestContext context);

}
