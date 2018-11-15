package com.rewedigital.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.util.response.ExtendableResponse;
import com.rewedigital.composer.util.response.ResponseExtension;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public interface RouteType {

    CompletionStage<ExtendableResponse<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final ResponseExtension extensions);
}
