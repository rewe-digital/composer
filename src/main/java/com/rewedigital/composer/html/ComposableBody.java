package com.rewedigital.composer.html;

import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.rewedigital.composer.composing.ContentRange;
import com.rewedigital.composer.composing.FragmentSource;
import com.rewedigital.composer.composing.IncludedFragment;
import com.rewedigital.composer.util.composable.Composable;
import com.rewedigital.composer.util.composable.CompositionStep;

public class ComposableBody implements Composable<ComposableBody>, FragmentSource {

    private static final ComposableBody empty = ComposableBody.of(CompositionStep.empty(), "",
            ContentRange.empty(), Collections.emptyList(), Collections.emptyList());

    private final CompositionStep step;
    private final List<Asset> assets;
    private final String template;
    private final ContentRange contentRange;
    private final List<IncludedFragment> includedFragments;
    private final List<ComposableBody> children;

    public static ComposableBody empty() {
        return empty;
    }

    public static ComposableBody of(final CompositionStep step, final String template,
            final ContentRange contentRange, final List<IncludedFragment> includedFragments, final List<Asset> assets) {
        return new ComposableBody(step, template, contentRange, includedFragments, assets, Collections.emptyList());
    }

    private ComposableBody(final CompositionStep step, final String template, final ContentRange contentRange,
            final List<IncludedFragment> includedFragments, final List<Asset> assets,
            final List<ComposableBody> children) {
        this.step = step;
        this.includedFragments = includedFragments;
        this.assets = assets;
        this.template = template;
        this.contentRange = contentRange;
        this.children = children;
    }

    @Override
    public ComposableBody composedWith(final ComposableBody other) {
        final LinkedList<ComposableBody> children = new LinkedList<>(this.children);
        children.add(other);
        return new ComposableBody(step, template, contentRange, includedFragments, assets, children);
    }

    @Override
    public List<IncludedFragment> includedFragments() {
        return includedFragments;
    }

    public String body() {
        final StringWriter writer = new StringWriter(template.length());
        int currentIndex = contentRange.start();
        for (final ComposableBody c : children) {
            writer.write(template, currentIndex, c.startOffset() - currentIndex);
            writer.write(c.body());
            currentIndex = c.endOffset();
        }
        writer.write(template, currentIndex, contentRange.end() - currentIndex);
        return writer.toString();
    }

    public Stream<Asset> allAssets() {
        return Stream.concat(assets.stream(), children.stream().flatMap(c -> c.allAssets()));
    }

    public boolean isEmpty() {
        return this == empty;
    }

    private int startOffset() {
        return step.startOffset();
    }

    private int endOffset() {
        return step.endOffset();
    }
}
