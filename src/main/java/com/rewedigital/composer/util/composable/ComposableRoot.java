package com.rewedigital.composer.util.composable;

import java.util.Optional;

import com.rewedigital.composer.composing.CompositionStep;
import com.spotify.apollo.Response;

/**
 * The root of a a group of {@link Composable}s. Individual {@link Composable}s
 * can be created using the root as well as merged into a root.
 *
 * @param <T>
 */
public interface ComposableRoot<T extends Composable<T>> {

    public ComposableRoot<T> composedWith(final T composable);

    public T composableFor(final Response<?> response, CompositionStep step);

    public Class<T> composableType();

    default public ComposableRoot<T> composedFrom(final Composables composables) {
        final Class<T> composableType = composableType();
        final Optional<ComposableRoot<T>> map = composables.get(composableType).map(r -> this.composedWith(r));
        return map.orElse(this);
    }

    public <P> Response<P> writtenTo(final Response<P> response);

}
