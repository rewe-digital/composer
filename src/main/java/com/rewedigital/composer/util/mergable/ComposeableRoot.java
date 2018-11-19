package com.rewedigital.composer.util.mergable;

import java.util.Optional;

import com.spotify.apollo.Response;

/**
 * The root of a a group of {@link Composable}s. Individual {@link Composable}s can
 * be created using the root as well as merged into a root.
 *
 * @param <T>
 */
public interface ComposeableRoot<T extends Composable<T>> {

    public ComposeableRoot<T> mergedWith(T mergable);

    public T mergableFor(final Response<?> response);

    public Class<T> mergableType();

    default ComposeableRoot<T> mergedFrom(final Mergables mergables) {
        Class<T> mergableType = mergableType();
        Optional<ComposeableRoot<T>> map = mergables.get(mergableType).map(r -> this.mergedWith((T) r));
        return map.orElse(this);
    }

    public <P> Response<P> writtenTo(Response<P> response);

}
