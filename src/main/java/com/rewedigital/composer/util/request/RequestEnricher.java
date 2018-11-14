package com.rewedigital.composer.util.request;

import com.spotify.apollo.Request;

public interface RequestEnricher {
    public Request enrich(final Request request);
}
