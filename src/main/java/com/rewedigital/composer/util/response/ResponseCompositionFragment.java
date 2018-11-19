package com.rewedigital.composer.util.response;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.rewedigital.composer.util.composable.Composable;
import com.rewedigital.composer.util.composable.Composables;

/**
 * Fragment of a {@link ResponseComposition}.
 *
 */
public class ResponseCompositionFragment implements Composables {

    private static final ResponseCompositionFragment empty = new ResponseCompositionFragment(Collections.emptyList());
    
    private final List<Composable<?>> composables;

    ResponseCompositionFragment(final List<Composable<?>> composeables) {
        this.composables = new LinkedList<>(composeables);
    }

    public static ResponseCompositionFragment empty() {
        return empty;
    }

    @SuppressWarnings("unchecked")
    public <Y extends Composable<?>> Optional<Y> get(final Class<Y> type) {
        for (Composable<?> element : composables) {
            if (type.isInstance(element)) {
                return (Optional<Y>) Optional.of(element);
            }
        }
        return Optional.empty();
    }

    public ResponseCompositionFragment composedWith(final ResponseCompositionFragment other) {
        List<Composable<?>> composed = new LinkedList<>();
        for (Composable<?> entry : composables) {
            composed.add(entry.mergedFrom(other));
        }

        for (Composable<?> entry : other.composables) {
            if (!get(entry.getClass()).isPresent()) {
                composed.add(entry);
            }
        }

        return new ResponseCompositionFragment(composed);
    }
}