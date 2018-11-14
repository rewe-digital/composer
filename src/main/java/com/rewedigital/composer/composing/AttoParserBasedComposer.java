package com.rewedigital.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.rewedigital.composer.parser.Parser;
import com.rewedigital.composer.util.response.ExtendableResponse;
import com.rewedigital.composer.util.response.ResponseExtension;
import com.spotify.apollo.Response;

/**
 * Implements the composer interfaces using the <code>atto parser</code> to parse html documents to identify include and content tags.
 */
public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposerHtmlConfiguration configuration;
    private final ResponseExtension extension;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher,  final ResponseExtension extension,
            final ComposerHtmlConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
        this.contentFetcher = new RecursionAwareContentFetcher(requireNonNull(contentFetcher),
                configuration.maxRecursion());

        this.extension = requireNonNull(extension);
    }

    @Override
    public CompletableFuture<ExtendableResponse<String>> composeTemplate(final Response<String> templateResponse,
            final String templatePath) {
        return parse(bodyOf(templateResponse), ContentRange.allUpToo(bodyOf(templateResponse).length()))
                .composeIncludes(contentFetcher, this, CompositionStep.root(templatePath))
                .thenApply(c -> c.withExtension(extension.fragmentFor(templateResponse)))
                .thenApply(c -> c.extract(response()))
                // temporary solution: correct cache-control header should be computed during
                // composition
                .thenApply(r -> r.transform(response -> response.withHeader("Cache-Control", "no-store,max-age=0")));
    }

    private Composition.Extractor<ExtendableResponse<String>> response() {
        return (payload, extensionFragment) -> new ExtendableResponse<String>(Response.forPayload(payload), 
                extension.mergedWith(extensionFragment));
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> contentResponse,
            final CompositionStep step) {
        return parse(bodyOf(contentResponse), ContentRange.empty()).composeIncludes(contentFetcher, this, step)
                .thenApply(c -> c.withExtension(extension.fragmentFor(contentResponse)));
    }

    private IncludeProcessor parse(final String template, final ContentRange defaultContentRange) {
        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(defaultContentRange, configuration);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler.buildProcessor(template);
    }

    private static String bodyOf(final Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }
}
