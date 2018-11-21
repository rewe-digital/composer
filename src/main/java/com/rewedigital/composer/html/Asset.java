package com.rewedigital.composer.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Describes a <code>link</code> or <code>script</code> tag found in an html <code>head</code> section during parsing of
 * a template or content fragment.
 */
class Asset {
    public static class Builder {

        private final String optionsAttributeName;
        private String type = "link";
        private boolean selfClosing = false;
        private final Map<String, String> attributes = new HashMap<>();

        public Builder(final String optionsAttributeName) {
            this.optionsAttributeName = optionsAttributeName;
        }

        public Asset.Builder type(final String type) {
            this.type = type;
            return this;
        }

        public Asset.Builder attribute(final String name, final String value) {
            this.attributes.put(name, value);
            return this;
        }

        public Asset.Builder selfClosing(final boolean selfClosing) {
            this.selfClosing = selfClosing;
            return this;
        }

        public boolean isInclude() {
            return optionsContain("include");
        }

        private boolean optionsContain(final String option) {
            return attributes.getOrDefault(optionsAttributeName, "").contains(option);
        }

        public Asset build() {
            return new Asset(this);
        }
    }

    private final String optionsAttributeName;
    private final String type;
    private final Map<String, String> attributes;
    private final boolean selfClosing;

    private Asset(final Asset.Builder builder) {
        this.optionsAttributeName = builder.optionsAttributeName;
        this.type = builder.type;
        this.selfClosing = builder.selfClosing;
        this.attributes = new HashMap<>(builder.attributes);
    }

    public String render() {
        return attributes
            .entrySet()
            .stream()
            .filter(notOptionsAttribute())
            .reduce(new StringBuilder().append(renderOpen()),
                (builder, e) -> builder.append(e.getKey())
                    .append("=\"")
                    .append(e.getValue())
                    .append("\" "),
                (a, b) -> a.append(b))
            .append(renderClosing()).toString();
    }

    private Predicate<? super Entry<String, String>> notOptionsAttribute() {
        return e -> !e.getKey().equals(optionsAttributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, selfClosing, type);
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
        final Asset other = (Asset) obj;
        return selfClosing == other.selfClosing &&
            Objects.equals(attributes, other.attributes) &&
            Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "Asset [type=" + type + ", attributes=" + attributes + ", selfClosing=" + selfClosing + "]";
    }

    private String renderOpen() {
        return "<" + type + " ";
    }

    private String renderClosing() {
        if (selfClosing) {
            return "/>";
        }
        return "></" + type + ">";
    }
}
