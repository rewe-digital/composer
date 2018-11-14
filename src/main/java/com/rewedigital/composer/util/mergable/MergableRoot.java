package com.rewedigital.composer.util.mergable;

import java.util.Optional;

public interface MergableRoot<T extends Mergable<T>> {

    public MergableRoot<T> mergedWith(T mergable);

    public Class<T> mergableType();

    default MergableRoot<T> mergedFrom(final Mergables mergables) {
        Class<T> mergableType = mergableType();
        Optional<MergableRoot<T>> map = mergables.get(mergableType).map(r -> this.mergedWith((T) r));
        return map.orElse(this);
    }

}
