package com.rewedigital.composer.composing;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.rewedigital.composer.util.Streams;

/**
 * Processes a list of includes by fetching the content for each include and
 * building a composition object that describes the composition of the template
 * containing the includes and the content fetched.
 */
public class IncludeProcessor {

    private final List<IncludedFragment> includedFragments;
    private final ContentRange contentRange;
    private final String template;
    private final List<Asset> assets;

    public IncludeProcessor(final String template, final List<IncludedFragment> includedFragments,
            final ContentRange contentRange, final List<Asset> assets) {
        this.template = template;
        // TODO
        this.includedFragments = includedFragments.stream().filter(f -> f.isInRage(contentRange)).collect(toList());
        this.contentRange = contentRange;
        this.assets = assets;
    }

    public List<IncludedFragment> includedFragments() {
        return includedFragments;
    }

    public ContentRange contentRange() {
        return contentRange;
    }

    public List<Asset> assets() {
        return assets;
    }

    public String template() {
        return template;
    }

    public CompletableFuture<Composition> composeIncludes(final ContentFetcher contentFetcher,
            final ContentComposer composer, final CompositionStep step) {
        final Stream<CompletableFuture<Composition>> composedIncludes = includedFragments.stream()
                .filter(s -> s.isInRage(contentRange))
                .map(s -> s.fetch(contentFetcher, step).thenCompose(r -> r.compose(composer)));
        return Streams.flatten(composedIncludes)
                .thenApply(c -> Composition.of(template, contentRange, assets, c.collect(toList())));
    }
}
