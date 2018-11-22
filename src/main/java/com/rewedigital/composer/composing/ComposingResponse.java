package com.rewedigital.composer.composing;

import java.util.Objects;
import java.util.Optional;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

/**
 * Holds a response with payload of type <code>T</code> and extending data via
 * an {@link ResponseComposition}.
 */
public class ComposingResponse<T> {

    private final Response<T> response;
    private final ResponseComposition composition;

    public static <T> ComposingResponse<T> composedFrom(final Response<T> response,
            final ResponseComposition composition, final String path) {
        final ResponseCompositionFragment fragment = composition.fragmentFor(response, CompositionStep.root(path));
        return new ComposingResponse<>(response, composition).composedWith(fragment);
    }

    public static <T> ComposingResponse<T> of(final Response<T> response, final ResponseComposition composition) {
        return new ComposingResponse<>(response, composition);
    }

    private ComposingResponse(final Response<T> response, final ResponseComposition composition) {
        this.response = Objects.requireNonNull(response);
        this.composition = Objects.requireNonNull(composition);
    }

    public ComposingResponse<T> composedWith(final ResponseCompositionFragment fragment) {
        return new ComposingResponse<>(response, composition.composedWith(fragment));
    }

    public ComposingResponse<T> withResponse(final Response<T> response) {
        return new ComposingResponse<>(response, composition);
    }

    public ResponseCompositionFragment fragmentFor(final CompositionStep step) {
        return composition.fragmentFor(response, step);
    }

    public <S extends ComposableRoot<?>> Optional<S> getComposition(final Class<S> type) {
        return composition.get(type);
    }

    public Response<T> composedResponse() {
        return composition.writeTo(response);
    }

    public RequestEnricher requestEnricher() {
        return composition;
    }

    public Optional<ComposingResponse<String>> toComposablePayload() {
        return response.payload()
                .filter(__ -> response.status().code() == Status.OK.code())
                .filter(ByteString.class::isInstance)
                .map(ByteString.class::cast)
                .map(ByteString::utf8)
                .map(response::withPayload)
                .map(r -> new ComposingResponse<>(r, composition));
    }
}
