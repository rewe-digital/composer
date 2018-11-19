package com.rewedigital.composer.util.composeable;

import java.util.Optional;

/**
 * A collection of {@link Composable}s that can be queried for a specific type.
 *
 */
public interface Composeables {
    public <T extends Composable<?>> Optional<T> get(Class<T> type);
}