package com.rewedigital.composer.util.composable;

import java.util.Optional;

/**
 * A collection of {@link Composable}s that can be queried for a specific type.
 *
 */
public interface Composables {
    public <T extends Composable<?>> Optional<T> get(Class<T> type);
}