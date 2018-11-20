package com.rewedigital.composer.composing;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.rewedigital.composer.util.composable.CompositionStep;
import com.rewedigital.composer.util.response.ComposedResponse;
import com.rewedigital.composer.util.response.ResponseComposition;
import com.rewedigital.composer.util.response.ResponseCompositionFragment;
import com.spotify.apollo.Response;

public class Composer implements IncludedFragment.Composer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ResponseComposition responseComposition;

    public Composer(final ContentFetcher contentFetcher, final ResponseComposition responseComposition) {
        this.contentFetcher = contentFetcher;
        this.responseComposition = responseComposition;
    }

    @Override
    public CompletableFuture<ComposedResponse<String>> composeTemplate(final Response<String> templateResponse, final String path) {
        return compose(templateResponse, CompositionStep.root(path))
                .thenApply(fragment -> responseComposition.composedWith(fragment))
                .thenApply(composition -> new ComposedResponse<>(templateResponse, composition))
                .thenApply(r -> r.transform(s -> s.withHeader("Cache-Control", "no-store,max-age=0")));
    }

    @Override
    public CompletableFuture<ResponseCompositionFragment> compose(final Response<String> response,
            final CompositionStep step) {
        final ResponseCompositionFragment fragment = responseComposition.fragmentFor(response, step);
        return includesIn(fragment)
                .map(include -> include.fetch(contentFetcher, step)
                        .thenCompose(content -> content.compose(this)))
                .reduce(completedFuture(fragment), (a, b) -> a
                        .thenCombine(b, (x, y) -> x.composedWith(y)));
    }

    private Stream<IncludedFragment> includesIn(final ResponseCompositionFragment compositionFragment) {
        return compositionFragment
                .get(FragmentSource.class)
                .map(fragmentSource -> fragmentSource.includedFragments())
                .orElse(Collections.emptyList()).stream();
    }
}
