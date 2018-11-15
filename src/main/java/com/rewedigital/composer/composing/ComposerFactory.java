package com.rewedigital.composer.composing;

import java.util.Map;

import com.rewedigital.composer.util.response.ResponseExtension;
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
            final ResponseExtension extensions) {
        return new AttoParserBasedComposer(new ValidatingContentFetcher(client, parsedPathArguments, extensions),
                extensions, configuration);
    }

}
