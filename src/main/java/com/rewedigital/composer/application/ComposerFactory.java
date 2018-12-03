package com.rewedigital.composer.application;

import java.util.Map;

import com.rewedigital.composer.composing.Composer;
import com.rewedigital.composer.composing.ComposerHtmlConfiguration;
import com.rewedigital.composer.composing.ComposingResponse;
import com.rewedigital.composer.composing.TemplateComposer;
import com.spotify.apollo.Client;
import com.typesafe.config.Config;

/**
 * Creates a new {@link TemplateComposer} instance.
 *
 */
public class ComposerFactory implements TemplateComposer.Factory {

    private final ComposerHtmlConfiguration configuration;

    public ComposerFactory(final Config configuration) {
        this.configuration = ComposerHtmlConfiguration.fromConfig(configuration);
    }

    @Override
    public TemplateComposer build(final Client client, final String path, final Map<String, Object> parsedPathArguments,
            final ComposingResponse<String> composingResponse) {

        final ValidatingContentFetcher contentFetcher = new ValidatingContentFetcher(client, parsedPathArguments,
                composingResponse.requestEnricher(), configuration.maxRecursion());

        return new Composer(contentFetcher, composingResponse, path);
    }

}
