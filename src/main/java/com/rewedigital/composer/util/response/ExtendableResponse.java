package com.rewedigital.composer.util.response;

import java.util.Objects;
import java.util.function.Function;

import com.rewedigital.composer.session.SessionRoot;
import com.spotify.apollo.Response;

/**
 * Holds a response with payload of type <code>T</code> and extending data via an {@link ResponseExtension}.
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

    // FIXME
    public Response<T> writeSessionToResponse() {
        return extension.get(SessionRoot.class).map(s -> s.writeTo(response)).orElse(response);
    }
}
