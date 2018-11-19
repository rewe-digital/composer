package com.rewedigital.composer.routing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.rewedigital.composer.helper.Sessions;
import com.rewedigital.composer.session.SessionRoot;
import com.rewedigital.composer.util.response.ComposedResponse;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public class SessionAwareProxyClientTest {

    @Test
    public void fetchesTemplateHandlingSession() throws Exception {
        final String method = "GET";

        final Request expectedRequest =
            Request.forUri(aRouteMatch().expandedPath(), method).withHeader("x-rd-key", "value");
        final Response<ByteString> response =
            Response.ok().withPayload(ByteString.EMPTY).withHeader("x-rd-response-key", "other-value");

        final RequestContext context = contextWith(aClient(expectedRequest, response), method);
        final ComposedResponse<ByteString> templateResponse =
            new ExtensionAwareRequestClient().fetch(aRouteMatch(), context, session("x-rd-key", "value"))
                .toCompletableFuture()
                .get();

        final SessionRoot sessionRoot = templateResponse.extensions().get(SessionRoot.class).get();
        assertThat(sessionRoot.get("key")).contains("value");
        assertThat(sessionRoot.get("response-key")).contains("other-value");
    }

    private RouteMatch aRouteMatch() {
        return new RouteMatch(Match.of("https://some.path/test", RouteTypeName.TEMPLATE),
            Collections.emptyMap());
    }

    private RequestContext contextWith(final Client client, final String method) {
        final RequestContext context = mock(RequestContext.class);
        when(context.requestScopedClient()).thenReturn(client);
        final Request request = mock(Request.class);
        when(request.method()).thenReturn(method);
        when(context.request()).thenReturn(request);
        return context;
    }

    private Client aClient(final Request request, final Response<ByteString> response) {
        final Client client = mock(Client.class);
        when(client.send(request)).thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }

    private ResponseComposition session(final String key, final String value) {
        return ResponseComposition.of(asList(Sessions.sessionRoot(key, value)));
    }
}
