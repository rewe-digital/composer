package com.rewedigital.composer.composing;

import java.util.Optional;

/**
 * A collection of {@link Composable}s that can be queried for a specific type.
 *
 */
public interface Composables {
    public <T> Optional<T> get(Class<T> type);
}