package com.rewedigital.composer.util.composable;

/**
 * Describes a type of objects that can be merged with each other.
 *  
 * @param <T>
 */
public interface Composable<T extends Composable<T>> {

    public T composedWith(final T other);

    @SuppressWarnings("unchecked")
    default T composedFrom(final Composables composables) {
        return composables.get(this.getClass()).map(r -> this.composedWith((T) r)).orElse((T) this);
    }
}