package com.rewedigital.composer.response;

import java.util.Objects;
import java.util.function.Function;

import com.spotify.apollo.Response;

/**
 * Holds a response with payload of type <code>T</code> and extending data via
 * an {@link ResponseComposition}.
 */
public class ComposedResponse<T> {

    private final Response<T> response;
    private final ResponseComposition composition;

    public ComposedResponse(final Response<T> response, final ResponseComposition extension) {
        this.response = Objects.requireNonNull(response);
        this.composition = Objects.requireNonNull(extension);
    }

    // FIXME confusing when to use this vs composed response?
    public Response<T> response() {
        return response;
    }

    public ResponseComposition composition() {
        return composition;
    }

    public <S> ComposedResponse<S> transform(final Function<Response<T>, Response<S>> transformation) {
        return new ComposedResponse<S>(transformation.apply(response), composition);
    }

    public Response<T> composedResponse() {
        return composition.writeTo(response);
    }
}
