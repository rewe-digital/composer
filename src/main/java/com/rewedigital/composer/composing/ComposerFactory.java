package com.rewedigital.composer.composing;

import java.util.Map;

import com.rewedigital.composer.response.ComposingResponse;
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

    public TemplateComposer build(final Client client, final String path, final Map<String, Object> parsedPathArguments,
            final ComposingResponse<String> composingResponse) {

        final ValidatingContentFetcher contentFetcher = new ValidatingContentFetcher(client, parsedPathArguments,
                composingResponse.requestEnricher(), configuration.maxRecursion());

        return new Composer(contentFetcher, composingResponse, path);
    }

}
