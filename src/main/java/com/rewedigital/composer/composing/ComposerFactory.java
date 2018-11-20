package com.rewedigital.composer.composing;

import java.util.Map;

import com.rewedigital.composer.response.ResponseComposition;
import com.spotify.apollo.Client;
import com.typesafe.config.Config;

/**
 * Creates a new {@link TemplateComposer} instance.
 *
 */
public class ComposerFactory {

    private final ComposerHtmlConfiguration configuration;

    public ComposerFactory(final Config configuration) {
        this.configuration = ComposerHtmlConfiguration.fromConfig(configuration);
    }

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments,
            final ResponseComposition responseComposition) {
        return new Composer(
                new ValidatingContentFetcher(client, parsedPathArguments, responseComposition,
                        configuration.maxRecursion()),
                responseComposition);
    }

}
