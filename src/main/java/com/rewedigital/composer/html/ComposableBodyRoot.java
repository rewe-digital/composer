package com.rewedigital.composer.html;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rewedigital.composer.composing.ComposerHtmlConfiguration;
import com.rewedigital.composer.composing.ContentRange;
import com.rewedigital.composer.response.ComposableRoot;
import com.rewedigital.composer.response.CompositionStep;
import com.spotify.apollo.Response;

public class ComposableBodyRoot implements ComposableRoot<ComposableBody> {

    private final ComposerHtmlConfiguration configuration;
    private final List<ComposableBody> children;

    public static ComposableBodyRoot of(final ComposerHtmlConfiguration configuration) {
        return new ComposableBodyRoot(configuration, Collections.emptyList());
    }

    private ComposableBodyRoot(final ComposerHtmlConfiguration configuration, final List<ComposableBody> children) {
        this.configuration = Objects.requireNonNull(configuration);
        this.children = Objects.requireNonNull(children);
    }

    @Override
    public ComposableRoot<ComposableBody> composedWith(final ComposableBody composable) {
        if (composable.isEmpty()) {
            return this;
        }

        final List<ComposableBody> children = new LinkedList<>(this.children);
        children.add(composable);
        return new ComposableBodyRoot(configuration, children);
    }

    @Override
    public ComposableBody composableFor(final Response<?> response, final CompositionStep step) {
        return bodyOf(response)
                .map(body -> parse(body, step))
                .orElse(ComposableBody.empty());
    }

    private ComposableBody parse(final String template, final CompositionStep step) {
        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(defaultContentRangeFor(step, template),
                configuration);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler.result(template, step);
    }

    private ContentRange defaultContentRangeFor(final CompositionStep step, final String body) {
        return step.isRoot() ? ContentRange.allUpToo(body.length())
                : ContentRange.empty();
    }

    @Override
    public Class<ComposableBody> composableType() {
        return ComposableBody.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Response<P> writtenTo(final Response<P> response) {
        return response.payload()
                .filter(String.class::isInstance)
                .map(r -> response.withPayload(withAssetLinks(body())))
                .map(r -> (Response<P>) r)
                .orElse(response);
    }

    private String body() {
        return children.stream().map(c -> c.body()).reduce("", (s, t) -> s + t);
    }

    private String withAssetLinks(final String body) {
        final String renderedAssets = allAssets().distinct().map(Asset::render).collect(Collectors.joining("\n"));
        return body.replaceFirst("</head>", renderedAssets + "\n</head>");
    }

    private Stream<Asset> allAssets() {
        return children.stream().flatMap(c -> c.allAssets());
    }

    private static Optional<String> bodyOf(final Response<?> response) {
        return response
                .payload()
                .filter(String.class::isInstance)
                .map(String.class::cast);
    }
}
