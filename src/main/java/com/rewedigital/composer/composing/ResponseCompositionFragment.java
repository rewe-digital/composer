package com.rewedigital.composer.composing;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Fragment of a {@link ResponseComposition}.
 *
 */
public class ResponseCompositionFragment implements Composables {

    private final List<Composable<?>> composables;

    ResponseCompositionFragment(final List<Composable<?>> composables) {
        this.composables = new LinkedList<>(composables);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> Optional<Y> get(final Class<Y> type) {
        for (final Composable<?> element : composables) {
            if (type.isInstance(element)) {
                return (Optional<Y>) Optional.of(element);
            }
        }
        return Optional.empty();
    }

    public ResponseCompositionFragment composedWith(final ResponseCompositionFragment other) {
        final List<Composable<?>> composed = new LinkedList<>();
        for (final Composable<?> entry : composables) {
            composed.add(entry.composedFrom(other));
        }

        for (final Composable<?> entry : other.composables) {
            if (!get(entry.getClass()).isPresent()) {
                composed.add(entry);
            }
        }

        return new ResponseCompositionFragment(composed);
    }
}