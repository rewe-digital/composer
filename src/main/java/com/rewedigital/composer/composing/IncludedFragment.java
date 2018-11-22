package com.rewedigital.composer.composing;

import java.util.concurrent.CompletableFuture;

public interface IncludedFragment {

    interface Composer {
        CompletableFuture<ResponseCompositionFragment> compose(final ComposingResponse<String> response,
                final CompositionStep parentStep);
    }

    interface FragmentResponse {
        CompletableFuture<ResponseCompositionFragment> compose(final Composer composer,
                final ComposingResponse<String> parent);
    }

    CompletableFuture<FragmentResponse> fetch(final ContentFetcher contentFetcher, final CompositionStep step);

}
