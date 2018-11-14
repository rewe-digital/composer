package com.rewedigital.composer.util.response;

import static java.util.Arrays.asList;

import java.util.Objects;
import java.util.function.Function;

import com.rewedigital.composer.session.SessionRoot;
import com.spotify.apollo.Response;

/**
 * Holds a response with payload of type <code>T</code> and extending data via an {@link Extension}.
 */
public class ExtendableResponse<T> {

    private final Response<T> response;
    private final Extension extension;

    // FIXME
    public ExtendableResponse(final Response<T> response, final SessionRoot session) {
        this(response, Extension.of(asList(session)));
    }

    public ExtendableResponse(final Response<T> response, final Extension extension) {
        this.response = Objects.requireNonNull(response);
        this.extension = Objects.requireNonNull(extension);
    }

    public Response<T> response() {
        return response;
    }

    public Extension extensions() {
        return extension;
    }

    public <S> ExtendableResponse<S> transform(final Function<Response<T>, Response<S>> transformation) {
        return new ExtendableResponse<S>(transformation.apply(response), extension);
    }

    // FIXME
    public Response<T> writeSessionToResponse(final SessionRoot.Serializer serializer) {
        return extension.get(SessionRoot.class).get().writeTo(response, serializer);
    }
}
