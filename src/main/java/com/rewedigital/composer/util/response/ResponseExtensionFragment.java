package com.rewedigital.composer.util.response;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.rewedigital.composer.util.mergable.Mergable;
import com.rewedigital.composer.util.mergable.Mergables;

public class ResponseExtensionFragment implements Mergables {

    private static final ResponseExtensionFragment empty = new ResponseExtensionFragment(Collections.emptyList());
    
    private final List<Mergable<?>> extensions;

    ResponseExtensionFragment(final List<Mergable<?>> extensions) {
        this.extensions = new LinkedList<>(extensions);
    }

    public static ResponseExtensionFragment empty() {
        return empty;
    }

    @SuppressWarnings("unchecked")
    public <Y extends Mergable<?>> Optional<Y> get(final Class<Y> type) {
        for (Mergable<?> element : extensions) {
            if (type.isInstance(element)) {
                return (Optional<Y>) Optional.of(element);
            }
        }
        return Optional.empty();
    }

    public ResponseExtensionFragment mergedWith(final ResponseExtensionFragment other) {
        List<Mergable<?>> mergedExtensions = new LinkedList<>();
        for (Mergable<?> entry : extensions) {
            mergedExtensions.add(entry.mergedFrom(other));
        }

        for (Mergable<?> entry : other.extensions) {
            if (!get(entry.getClass()).isPresent()) {
                mergedExtensions.add(entry);
            }
        }

        return new ResponseExtensionFragment(mergedExtensions);
    }
}