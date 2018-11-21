package com.rewedigital.composer.response;

import static com.rewedigital.composer.util.Combiners.throwingCombiner;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

/**
 * Composition part of a {@link ComposingResponse}. Holds multiple
 * {@link ComposableRoot}s that form the base of the composition. Creates
 * {@link ResponseCompositionFragment}s based on the contained roots.
 *
 */
public class ResponseComposition implements RequestEnricher {

    private final Map<Class<ComposableRoot<?>>, ComposableRoot<?>> roots;

    private ResponseComposition(final Map<Class<ComposableRoot<?>>, ComposableRoot<?>> roots) {
        this.roots = roots;
    }

    public static ResponseComposition of(final List<ComposableRoot<?>> roots) {
        final Map<Class<ComposableRoot<?>>, ComposableRoot<?>> mappedRoots = new HashMap<>();
        for (final ComposableRoot<?> root : roots) {
            @SuppressWarnings("unchecked")
            final Class<ComposableRoot<?>> type = (Class<ComposableRoot<?>>) root.getClass();
            if (mappedRoots.containsKey(type)) {
                throw new IllegalArgumentException(
                        "each type of MergableRoot<> must only appear at most once in parameter roots!");
            }
            mappedRoots.put(type, root);
        }
        return new ResponseComposition(mappedRoots);
    }

    ResponseCompositionFragment fragmentFor(final Response<?> response, final CompositionStep step) {
        return new ResponseCompositionFragment(
                roots.values().stream()
                        .map(r -> r.composableFor(response, step))
                        .collect(toList()));
    }

    ResponseComposition composedWith(final ResponseCompositionFragment fragment) {
        return ResponseComposition.of(roots.values().stream().map(r -> r.composedFrom(fragment)).collect(toList()));
    }

    @SuppressWarnings("unchecked")
    <Y extends ComposableRoot<?>> Optional<Y> get(final Class<Y> type) {
        return Optional.ofNullable((Y) roots.get(type));
    }

    @Override
    public Request enrich(final Request request) {
        return roots.values().stream().filter(RequestEnricher.class::isInstance).reduce(request,
                (req, root) -> ((RequestEnricher) root).enrich(req), throwingCombiner());
    }

    <P> Response<P> writeTo(final Response<P> response) {
        return roots.values().stream().reduce(response, (resp, root) -> root.writtenTo(resp), throwingCombiner());
    }
}
