package com.rewedigital.composer;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposerHtmlConfiguration;
import com.rewedigital.composer.composing2.ComposerFactory;
import com.rewedigital.composer.html.ComposableBodyRoot;
import com.rewedigital.composer.proxy.ComposingRequestHandler;
import com.rewedigital.composer.routing.BackendRouting;
import com.rewedigital.composer.routing.ExtensionAwareRequestClient;
import com.rewedigital.composer.routing.RouteTypes;
import com.rewedigital.composer.session.CookieBasedSessionHandler;
import com.rewedigital.composer.session.SessionHandler;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.rewedigital.composer.util.response.ResponseCompositionHandler;
import com.spotify.apollo.RequestContext;
import com.typesafe.config.Config;

public class RequestHandlerFactory {

    public static ComposingRequestHandler createRequestHandler(final Config configuration) {
        final ComposingRequestHandler handler = new ComposingRequestHandler(
                new BackendRouting(configuration.getConfig("composer.routing")),
                new RouteTypes(new ComposerFactory(configuration.getConfig("composer.html")),
                        new ExtensionAwareRequestClient()),
                responseExtensions(configuration));
        return handler;
    }

    public static ResponseCompositionHandler responseExtensions(final Config configuration) {
        final SessionHandler sessionHandler = CookieBasedSessionHandler
                .create(configuration.getConfig("composer.session"));
        return new ResponseCompositionHandler() {
            @Override
            public CompletionStage<ResponseComposition> initializeFrom(final RequestContext context) {
                return sessionHandler.initialize(context)
                        .thenApply(session -> ResponseComposition.of(Arrays.asList(new ComposableBodyRoot(
                                ComposerHtmlConfiguration.fromConfig(configuration.getConfig("composer.html"))),
                                session)));
            }

        };
    }
}
