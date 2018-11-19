package com.rewedigital.composer.util.mergable;

import java.util.Optional;

/**
 * A collection of {@link Composable}s that can be queried for a specific type.
 *
 */
public interface Mergables {
    public <T extends Composable<?>> Optional<T> get(Class<T> type);
}