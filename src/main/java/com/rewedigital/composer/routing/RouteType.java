package com.rewedigital.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.util.response.ResponseComposition;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public interface RouteType {

    CompletionStage<Response<ByteString>> execute(final RouteMatch rm, final RequestContext context,
            final ResponseComposition composition);
}
