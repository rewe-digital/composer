package com.rewedigital.composer.html;

import java.util.LinkedList;
import java.util.List;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;

import com.rewedigital.composer.composing.ComposerHtmlConfiguration;

class ContentMarkupHandler extends AbstractMarkupHandler {

    private final char[] contentTag;
    private final String assetOptionsAttribute;

    private final List<Asset> assets = new LinkedList<>();

    private Asset.Builder current = null;
    private final ContentRange defaultContentRange;

    private int contentStart = 0;
    private int contentEnd = 0;

    private boolean parsingHead = false;


    public ContentMarkupHandler(final ContentRange defaultContentRange, final ComposerHtmlConfiguration configuration) {
        this.defaultContentRange = defaultContentRange;
        this.contentTag = configuration.contentTag().toCharArray();
        this.assetOptionsAttribute = configuration.assetOptionsAttribute();
    }

    public ContentRange contentRange() {
        return contentEnd <= 0 ? defaultContentRange : new ContentRange(contentStart, contentEnd);
    }

    public List<Asset> assets() {
        return assets;
    }

    @Override
    public void handleStandaloneElementStart(final char[] buffer, final int nameOffset, final int nameLen,
        final boolean minimized, final int line, final int col) throws ParseException {
        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        if (parsingHead && isAssetElement(buffer, nameOffset, nameLen)) {
            startAsset(buffer, nameOffset, nameLen, true);
        }
    }

    @Override
    public void handleStandaloneElementEnd(final char[] buffer, final int nameOffset, final int nameLen,
        final boolean minimized, final int line,
        final int col) throws ParseException {
        super.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
        if (parsingAsset()) {
            pushAsset();
        }
    }

    @Override
    public void handleOpenElementStart(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col) throws ParseException {
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);

        if (isHeadElement(buffer, nameOffset, nameLen)) {
            parsingHead = true;
        } else if (isContentElement(buffer, nameOffset, nameLen)) {
            contentStart = nameOffset + nameLen + 1;
        } else if (parsingHead && isAssetElement(buffer, nameOffset, nameLen)) {
            startAsset(buffer, nameOffset, nameLen, false);
        }
    }

    @Override
    public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col) throws ParseException {
        super.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        if (isHeadElement(buffer, nameOffset, nameLen)) {
            parsingHead = false;
        } else if (isContentElement(buffer, nameOffset, nameLen) && contentStart >= 0) {
            contentEnd = nameOffset - 2;
        } else if (parsingAsset()) {
            pushAsset();
        }
    }

    @Override
    public void handleAttribute(final char[] buffer, final int nameOffset, final int nameLen, final int nameLine,
        final int nameCol,
        final int operatorOffset, final int operatorLen, final int operatorLine, final int operatorCol,
        final int valueContentOffset,
        final int valueContentLen, final int valueOuterOffset, final int valueOuterLen, final int valueLine,
        final int valueCol)
        throws ParseException {
        super.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine,
            operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

        if (parsingAsset()) {
            current = current.attribute(new String(buffer, nameOffset, nameLen),
                new String(buffer, valueContentOffset, valueContentLen));
        }
    }

    private boolean parsingAsset() {
        return current != null;
    }

    private boolean isAssetElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return textContains(buffer, nameOffset, nameLen, "link") || textContains(buffer, nameOffset, nameLen, "script");
    }

    private boolean isHeadElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return textContains(buffer, nameOffset, nameLen, "head");
    }

    private boolean textContains(final char[] buffer, final int nameOffset, final int nameLen, final String item) {
        return TextUtil.contains(true, buffer, nameOffset, nameLen, item, 0, item.length());
    }

    private boolean isContentElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return contentEnd <= 0
            && TextUtil.contains(true, buffer, nameOffset, nameLen, contentTag, 0, contentTag.length);
    }


    private void startAsset(final char[] buffer, final int nameOffset, final int nameLen, final boolean selfClosing) {
        current = new Asset.Builder(assetOptionsAttribute)
            .type(new String(buffer, nameOffset, nameLen))
            .selfClosing(selfClosing);
    }

    private void pushAsset() {
        if (current.isInclude()) {
            assets.add(current.build());
        }
        current = null;
    }



}
