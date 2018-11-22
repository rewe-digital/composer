package com.rewedigital.composer.composing;

import com.spotify.apollo.Request;

/**
 * Interface describing something that can enrich a {@link Request}.
 *
 */
public interface RequestEnricher {
    public Request enrich(final Request request);
}
