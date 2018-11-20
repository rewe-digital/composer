package com.rewedigital.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.rewedigital.composer.parser.Parser;
import com.rewedigital.composer.util.response.ComposedResponse;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.spotify.apollo.Response;

/**
 * Implements the composer interfaces using the <code>atto parser</code> to
 * parse html documents to identify include and content tags.
 */
public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposerHtmlConfiguration configuration;
    private final ResponseComposition responseComposition;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher, final ResponseComposition extension,
            final ComposerHtmlConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
        this.contentFetcher = new RecursionAwareContentFetcher(requireNonNull(contentFetcher),
                configuration.maxRecursion());

        this.responseComposition = requireNonNull(extension);
    }

    @Override
    public CompletableFuture<ComposedResponse<String>> composeTemplate(final Response<String> templateResponse,
            final String templatePath) {
        return parse(bodyOf(templateResponse), ContentRange.allUpToo(bodyOf(templateResponse).length()))
                .composeIncludes(contentFetcher, this, CompositionStep.root(templatePath))
                .thenApply(c -> c.withExtension(
                        responseComposition.fragmentFor(templateResponse, CompositionStep.root(templatePath))))
                .thenApply(c -> c.extract(response()))
                // temporary solution: correct cache-control header should be computed during
                // composition
                .thenApply(r -> r.transform(response -> response.withHeader("Cache-Control", "no-store,max-age=0")));
    }

    private Composition.Extractor<ComposedResponse<String>> response() {
        return (payload, extensionFragment) -> {
            try {
                final ResponseComposition responseComposition = this.responseComposition
                        .composedWith(CompletableFuture.completedFuture(extensionFragment)).get(); // FIXME!!!
                return new ComposedResponse<>(Response.forPayload(payload), responseComposition);
            } catch (final Exception e) {
                throw new RuntimeException(e); // FIXME
            }
        };
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> contentResponse,
            final CompositionStep step) {
        return parse(bodyOf(contentResponse), ContentRange.empty()).composeIncludes(contentFetcher, this, step)
                .thenApply(c -> c.withExtension(responseComposition.fragmentFor(contentResponse, step)));
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
