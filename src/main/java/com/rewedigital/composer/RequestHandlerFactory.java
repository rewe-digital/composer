package com.rewedigital.composer;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposerFactory;
import com.rewedigital.composer.proxy.ComposingRequestHandler;
import com.rewedigital.composer.routing.BackendRouting;
import com.rewedigital.composer.routing.ExtensionAwareRequestClient;
import com.rewedigital.composer.routing.RouteTypes;
import com.rewedigital.composer.session.CookieBasedSessionHandler;
import com.rewedigital.composer.session.SessionHandler;
import com.rewedigital.composer.util.response.ResponseExtension;
import com.rewedigital.composer.util.response.ResponseExtensionHandler;
import com.spotify.apollo.RequestContext;
import com.typesafe.config.Config;

public class RequestHandlerFactory {


    public static ComposingRequestHandler createRequestHandler(Config configuration) {
        final ComposingRequestHandler handler =
            new ComposingRequestHandler(
                new BackendRouting(configuration.getConfig("composer.routing")),
                new RouteTypes(
                    new ComposerFactory(configuration.getConfig("composer.html")),
                    new ExtensionAwareRequestClient()),
                responseExtensions(configuration));
        return handler;
    }

    public static ResponseExtensionHandler responseExtensions(Config configuration) {
        final SessionHandler sessionHandler =
            new CookieBasedSessionHandler.Factory(configuration.getConfig("composer.session")).build();
        return new ResponseExtensionHandler() {
            @Override
            public CompletionStage<ResponseExtension> initialize(RequestContext context) {
                return sessionHandler.initialize(context)
                    .thenApply(session -> ResponseExtension.of(Arrays.asList(session)));
            }

        };
    }
}
