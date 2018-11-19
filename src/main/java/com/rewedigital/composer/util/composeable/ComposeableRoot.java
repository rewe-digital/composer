package com.rewedigital.composer.util.composeable;

import java.util.Optional;

import com.spotify.apollo.Response;

/**
 * The root of a a group of {@link Composable}s. Individual {@link Composable}s can
 * be created using the root as well as merged into a root.
 *
 * @param <T>
 */
public interface ComposeableRoot<T extends Composable<T>> {

    public ComposeableRoot<T> composedWith(T composeable);

    public T composeableFor(final Response<?> response);

    public Class<T> composeableType();

    default ComposeableRoot<T> composedFrom(final Composeables composeables) {
        Class<T> composeableType = composeableType();
        Optional<ComposeableRoot<T>> map = composeables.get(composeableType).map(r -> this.composedWith((T) r));
        return map.orElse(this);
    }

    public <P> Response<P> writtenTo(Response<P> response);

}
