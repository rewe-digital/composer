package com.rewedigital.composer.composing;

import static com.rewedigital.composer.parser.Parser.PARSER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ContentMarkupHandlerTest {

    private static final ContentRange defaultContentRange = new ContentRange(123, 456);

    @Test
    public void parsesAssetsFromHead() {
        final List<Asset> result = parse("<!DOCTYPE html><html>\n" +
            "<head>"
            + "<link href=\"../static/css/core.css\" data-rd-options=\"include\" rel=\"stylesheet\" media=\"screen\" />"
            + "<script href=\"../static/js/other.js\" data-rd-options=\"include\" ></script>"
            + "<link href=\"../static/css/removed_from_head.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</head>\n" +
            "<body>"
            + "<link href=\"../static/css/removed_from_body.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</body></html>").assets();

        assertEquals(
            Arrays.asList(
                new Asset.Builder("data-rd-options").type("link").attribute("rel", "stylesheet")
                    .attribute("data-rd-options", "include").attribute("href", "../static/css/core.css")
                    .attribute("media", "screen").selfClosing(true).build(),
                new Asset.Builder("data-rd-options").type("script").attribute("data-rd-options", "include")
                    .attribute("href", "../static/js/other.js").selfClosing(false).build()),
            result);
    }

    @Test
    public void parsesContentRangeFromMarkup() {
        final ContentRange content = parse("aa<rewe-digital-content>test</rewe-digital-content>bb").contentRange();
        assertThat(content).isEqualTo(new ContentRange(24, 28));
    }

    @Test
    public void usesDefaultContentRangeIfMissingContentTags() {
        final ContentRange content = parse("aa test bb").contentRange();
        assertThat(content).isEqualTo(defaultContentRange);
    }

    private ContentMarkupHandler parse(final String data) {
        final ContentMarkupHandler markupHandler =
            new ContentMarkupHandler(defaultContentRange,
                new ComposerHtmlConfiguration("", "rewe-digital-content", "data-rd-options", 1));
        PARSER.parse(data, markupHandler);
        return markupHandler;
    }

}
