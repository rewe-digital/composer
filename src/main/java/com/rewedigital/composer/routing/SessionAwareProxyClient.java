package com.rewedigital.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.session.ResponseWithSession;
import com.rewedigital.composer.session.SessionFragment;
import com.rewedigital.composer.session.SessionRoot;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class SessionAwareProxyClient {

    public CompletionStage<ResponseWithSession<ByteString>> fetch(final String path, final RequestContext context,
        final SessionRoot session) {
        return context.requestScopedClient()
            .send(session.enrich(Request.forUri(path, context.request().method())))
            .thenApply(r -> new ResponseWithSession<>(r, session.mergedWith(SessionFragment.of(r))));
    }
}