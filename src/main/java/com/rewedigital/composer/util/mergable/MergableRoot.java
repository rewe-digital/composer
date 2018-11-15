package com.rewedigital.composer.util.mergable;

import java.util.Optional;

import com.spotify.apollo.Response;

/**
 * The root of a a group of {@link Mergable}s. Individual {@link Mergable}s can
 * be created using the root as well as merged into a root.
 *
 * @param <T>
 */
public interface MergableRoot<T extends Mergable<T>> {

    public MergableRoot<T> mergedWith(T mergable);

    public T mergableFor(final Response<?> response);

    public Class<T> mergableType();

    default MergableRoot<T> mergedFrom(final Mergables mergables) {
        Class<T> mergableType = mergableType();
        Optional<MergableRoot<T>> map = mergables.get(mergableType).map(r -> this.mergedWith((T) r));
        return map.orElse(this);
    }

    public <P> Response<P> writtenTo(Response<P> response);

}
