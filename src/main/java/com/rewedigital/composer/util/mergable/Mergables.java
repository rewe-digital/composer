package com.rewedigital.composer.util.mergable;

import java.util.Optional;

/**
 * A collection of {@link Mergable}s that can be queried for a specific type.
 *
 */
public interface Mergables {
    public <T extends Mergable<?>> Optional<T> get(Class<T> type);
}