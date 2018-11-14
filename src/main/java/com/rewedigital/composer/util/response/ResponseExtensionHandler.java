package com.rewedigital.composer.util.response;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.RequestContext;

public interface ResponseExtensionHandler {

    public CompletionStage<ResponseExtension> initialize(final RequestContext context);

}
