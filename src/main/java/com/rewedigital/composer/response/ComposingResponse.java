package com.rewedigital.composer.response;

import java.util.Objects;
import java.util.function.Function;

import com.spotify.apollo.Response;

/**
 * Holds a response with payload of type <code>T</code> and extending data via
 * an {@link ResponseComposition}.
 */
public class ComposingResponse<T> {

    private final Response<T> response;
    private final ResponseComposition composition;

    public static <T> ComposingResponse<T> composedFrom(final Response<T> response,
            final ResponseComposition composition, final String path) {
        return new ComposingResponse<>(response, composition.composedWithFragmentFor(response, path));
    }

    public ComposingResponse(final Response<T> response, final ResponseComposition composition) {
        this.response = Objects.requireNonNull(response);
        this.composition = Objects.requireNonNull(composition);
    }

    // FIXME confusing when to use this vs composed response?
    public Response<T> response() {
        return response;
    }

    public ResponseComposition composition() {
        return composition;
    }

    public <S> ComposingResponse<S> transform(final Function<Response<T>, Response<S>> transformation) {
        return new ComposingResponse<S>(transformation.apply(response), composition);
    }

    public Response<T> composedResponse() {
        return composition.writeTo(response);
    }
}
