package com.rewedigital.composer;

import static java.util.Arrays.asList;

import java.util.concurrent.CompletionStage;

import com.rewedigital.composer.composing.ComposerFactory;
import com.rewedigital.composer.composing.ComposerHtmlConfiguration;
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

    private static class CompositionHandler implements ResponseCompositionHandler {

        private final SessionHandler sessionHandler;
        private final ComposerHtmlConfiguration htmlConfig;

        public CompositionHandler(final SessionHandler sessionHandler, final Config htmlConfig) {
            this.sessionHandler = sessionHandler;
            this.htmlConfig = ComposerHtmlConfiguration.fromConfig(htmlConfig);
        }

        @Override
        public CompletionStage<ResponseComposition> initializeFrom(final RequestContext context) {
            return sessionHandler.initialize(context)
                    .thenApply(session -> ResponseComposition.of(asList(
                            ComposableBodyRoot.of(htmlConfig),
                            session)));
        }
    }

    public static ComposingRequestHandler createRequestHandler(final Config configuration) {
        final Config routingConfig = configuration.getConfig("composer.routing");
        final Config htmlConfig = configuration.getConfig("composer.html");
        final Config sessionConfig = configuration.getConfig("composer.session");

        final BackendRouting routing = new BackendRouting(routingConfig);
        final ComposerFactory composerFactory = new ComposerFactory(htmlConfig);
        final ExtensionAwareRequestClient templateClient = new ExtensionAwareRequestClient();
        final RouteTypes routeTypes = new RouteTypes(composerFactory, templateClient);

        final SessionHandler sessionHandler = CookieBasedSessionHandler.create(sessionConfig);
        final ResponseCompositionHandler compositionHandler = new CompositionHandler(sessionHandler, htmlConfig);

        return new ComposingRequestHandler(routing, routeTypes, compositionHandler);
    }
}
