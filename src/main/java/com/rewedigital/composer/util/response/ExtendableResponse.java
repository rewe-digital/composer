package com.rewedigital.composer.util.response;

import java.util.Objects;
import java.util.function.Function;

import com.spotify.apollo.Response;

/**
 * Holds a response with payload of type <code>T</code> and extending data via
 * an {@link ResponseExtension}.
 */
public class ExtendableResponse<T> {

    private final Response<T> response;
    private final ResponseExtension extension;

    public ExtendableResponse(final Response<T> response, final ResponseExtension extension) {
        this.response = Objects.requireNonNull(response);
        this.extension = Objects.requireNonNull(extension);
    }

    public Response<T> response() {
        return response;
    }

    public ResponseExtension extensions() {
        return extension;
    }

    public <S> ExtendableResponse<S> transform(final Function<Response<T>, Response<S>> transformation) {
        return new ExtendableResponse<S>(transformation.apply(response), extension);
    }

    public Response<T> extendedResponse() {
        return extension.writeTo(response);
    }
}
