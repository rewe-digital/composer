package com.rewedigital.composer.util.composable;

import java.util.Objects;

/**
 * Describes a step in the composition process. The composition is done
 * recursively, and steps are created root to leaf. It is used to trace the
 * progress of the recursive composition. .
 */
public class CompositionStep {

    private static class Position {
        private final int start;
        private final int end;

        private Position(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
    }

    private final CompositionStep parent;
    private final String path;
    private final int depth;
    private final Position position;

    public static CompositionStep empty() {
        return root(null);
    }

    public static CompositionStep root(final String path) {
        return new CompositionStep(null, path, new Position(-1, -1), 0);
    }

    private CompositionStep(final CompositionStep parent, final String path, final Position position, final int depth) {
        this.parent = parent;
        this.path = path;
        this.position = position;
        this.depth = depth;
    }

    public int depth() {
        return depth;
    }

    public int startOffset() {
        return this.position.start;
    }

    public int endOffset() {
        return this.position.end;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isEmpty() {
        return path == null && isRoot();
    }

    public CompositionStep childWith(final String path, final int startOffset, final int endOffset) {
        return new CompositionStep(this, path, new Position(startOffset, endOffset), depth + 1);
    }

    public String callStack() {
        if (isEmpty()) {
            return "";
        }

        if (isRoot()) {
            return "[" + path + "]";
        }

        return "[" + path + "] included via " + parent.callStack();
    }

    @Override
    public String toString() {
        return callStack();
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, path, depth, position.start, position.end);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompositionStep other = (CompositionStep) obj;
        return depth == other.depth &&
                position.start == other.position.start &&
                position.end == other.position.end &&
                Objects.equals(path, other.path) &&
                Objects.equals(parent, other.parent);
    }

}
