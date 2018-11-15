package com.rewedigital.composer.util.response;

import static com.rewedigital.composer.util.Combiners.throwingCombiner;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rewedigital.composer.util.mergable.MergableRoot;
import com.rewedigital.composer.util.request.RequestEnricher;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

/**
 * Extension of a {@link ExtendableResponse}. Holds multiple
 * {@link MergableRoot}s that form the base of the extensions. Creates
 * {@link ResponseExtensionFragment}s based on the contained roots.
 *
 */
public class ResponseExtension implements RequestEnricher {

    private final Map<Class<MergableRoot<?>>, MergableRoot<?>> roots;

    private ResponseExtension(final Map<Class<MergableRoot<?>>, MergableRoot<?>> roots) {
        this.roots = roots;
    }

    public static ResponseExtension of(final List<MergableRoot<?>> roots) {
        Map<Class<MergableRoot<?>>, MergableRoot<?>> mappedRoots = new HashMap<>();
        for (MergableRoot<?> root : roots) {
            @SuppressWarnings("unchecked")
            Class<MergableRoot<?>> type = (Class<MergableRoot<?>>) root.getClass();
            if (mappedRoots.containsKey(type)) {
                throw new IllegalArgumentException(
                        "each type of MergableRoot<> must only appear at most once in parameter roots!");
            }
            mappedRoots.put(type, root);
        }
        return new ResponseExtension(mappedRoots);
    }

    public ResponseExtension mergedWithFragmentFor(final Response<?> response) {
        return this.mergedWith(fragmentFor(response));
    }

    public ResponseExtensionFragment fragmentFor(final Response<?> response) {
        return new ResponseExtensionFragment(
                roots.values().stream().map(r -> r.mergableFor(response)).collect(toList()));
    }

    public ResponseExtension mergedWith(final ResponseExtensionFragment fragment) {
        return ResponseExtension.of(roots.values().stream().map(r -> r.mergedFrom(fragment)).collect(toList()));
    }

    @SuppressWarnings("unchecked")
    public <Y extends MergableRoot<?>> Optional<Y> get(final Class<Y> type) {
        return Optional.ofNullable((Y) roots.get(type));
    }

    @Override
    public Request enrich(Request request) {
        return roots.values().stream().filter(RequestEnricher.class::isInstance).reduce(request,
                (req, root) -> ((RequestEnricher) root).enrich(req), throwingCombiner());
    }

    public <P> Response<P> writeTo(final Response<P> response) {
        return roots.values().stream().reduce(response, (resp, root) -> root.writtenTo(resp), throwingCombiner());

    }
}
