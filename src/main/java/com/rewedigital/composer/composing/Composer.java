package com.rewedigital.composer.composing;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Composer implements IncludedFragment.Composer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposingResponse<String> composingResponse;
    private final String path;

    public Composer(final ContentFetcher contentFetcher, final ComposingResponse<String> composingResponse,
            final String path) {
        this.contentFetcher = contentFetcher;
        this.composingResponse = composingResponse;
        this.path = path;
    }

    @Override
    public CompletableFuture<ComposingResponse<String>> composeTemplate() {
        return compose(composingResponse, CompositionStep.root(path))
                .thenApply(fragment -> composingResponse.composedWith(fragment));
    }

    @Override
    public CompletableFuture<ResponseCompositionFragment> compose(final ComposingResponse<String> response,
            final CompositionStep step) {
        final ResponseCompositionFragment fragment = response.fragmentFor(step);
        return includesIn(fragment)
                .map(include -> include.fetch(contentFetcher, step)
                        .thenCompose(content -> content.compose(this, response)))
                .reduce(completedFuture(fragment), (a, b) -> a
                        .thenCombine(b, (x, y) -> x.composedWith(y)));
    }

    private Stream<? extends IncludedFragment> includesIn(final ResponseCompositionFragment compositionFragment) {
        return compositionFragment
                .get(FragmentSource.class)
                .map(fragmentSource -> fragmentSource.includedFragments())
                .orElse(Collections.emptyList()).stream();
    }
}
