package com.rewedigital.composer.util.response;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.rewedigital.composer.util.mergable.MergableRoot;
import com.rewedigital.composer.util.request.RequestEnricher;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class ResponseExtension implements RequestEnricher {

    // FIXME allow only on instance per type!
    private final List<MergableRoot<?>> roots;

    private ResponseExtension(final List<MergableRoot<?>> roots) {
        this.roots = new LinkedList<>(roots);
    }

    public static ResponseExtension of(final List<MergableRoot<?>> roots) {
        return new ResponseExtension(roots);
    }

    public ResponseExtension mergedWithFragmentFor(final Response<?> response) {
        return this.mergedWith(fragmentFor(response));
    }

    public ResponseExtensionFragment fragmentFor(final Response<?> response) {
        return new ResponseExtensionFragment(roots.stream().map(r -> r.mergableFor(response)).collect(toList()));
    }

    public ResponseExtension mergedWith(final ResponseExtensionFragment fragment) {
        return ResponseExtension.of(roots.stream().map(r -> r.mergedFrom(fragment)).collect(toList()));
    }

    @SuppressWarnings("unchecked")
    public <Y extends MergableRoot<?>> Optional<Y> get(final Class<Y> type) {
        for (MergableRoot<?> element : roots) {
            if (type.isInstance(element)) { // FIXME optimize by building up a map type -> instance
                return (Optional<Y>) Optional.of(element);
            }
        }
        return Optional.empty();
    }

    @Override
    public Request enrich(Request request) {
        Request result = request;
        for (Object element : roots) {
            if (RequestEnricher.class.isInstance(element)) {
                result = ((RequestEnricher) element).enrich(result);
            }
        }
        return result;
    }
}
