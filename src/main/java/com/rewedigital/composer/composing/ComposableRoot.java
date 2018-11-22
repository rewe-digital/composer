package com.rewedigital.composer.composing;

import com.spotify.apollo.Response;

/**
 * The root of a a group of {@link Composable}s. Individual {@link Composable}s
 * can be created using the root as well as composed into a root.
 *
 * @param <T>
 */
public interface ComposableRoot<T extends Composable<T>> {

    public ComposableRoot<T> composedWith(final T composable);

    public T composableFor(final Response<?> response, CompositionStep step);

    public Class<T> composableType();

    default public ComposableRoot<T> composedFrom(final Composables composables) {
        return composables
                .get(composableType())
                .map(r -> this.composedWith(r))
                .orElse(this);
    }

    public <P> Response<P> writtenTo(final Response<P> response);

}
