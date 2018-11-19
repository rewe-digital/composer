package com.rewedigital.composer.util.composable;

/**
 * Describes a type of objects that can be merged with each other.
 *  
 * @param <T>
 */
public interface Composable<T extends Composable<T>> {

    public T mergedWith(final T other);

    @SuppressWarnings("unchecked")
    default T mergedFrom(final Composables mergables) {
        return mergables.get(this.getClass()).map(r -> this.mergedWith((T) r)).orElse((T) this);
    }
}