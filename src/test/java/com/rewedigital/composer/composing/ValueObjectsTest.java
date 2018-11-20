package com.rewedigital.composer.composing;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import java.time.Duration;
import java.util.Optional;

import org.junit.Test;

import com.rewedigital.composer.html.Asset;
import com.rewedigital.composer.util.composable.CompositionStep;

public class ValueObjectsTest {

    @Test
    public void contentRange_is_value_object() {
        final ContentRange first = new ContentRange(0, 10);
        final ContentRange second = new ContentRange(0, 10);
        final ContentRange third = new ContentRange(10, 20);
        final ContentRange forth = new ContentRange(5, 10);
        final ContentRange fifth = new ContentRange(0, 20);

        assertIsValueObject(first, second, third, forth, fifth);
    }

    @Test
    public void fetchContext_is_value_object() {
        final FetchContext first = FetchContext.of("/", "/fallback", Optional.empty());
        final FetchContext second = FetchContext.of("/", "/fallback", Optional.empty());
        final FetchContext third = FetchContext.of("/test", "/fallback", Optional.empty());
        final FetchContext forth = FetchContext.of("/", "/other", Optional.empty());
        final FetchContext fifth = FetchContext.of("/", "/fallback", Optional.of(Duration.ofMillis(1000)));

        assertIsValueObject(first, second, third, forth, fifth);
    }

    @Test
    public void asset_is_value_object() {
        final Asset first = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        final Asset second = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        final Asset third = new Asset.Builder("xyz").attribute("third_attr", "first_value").type("test").build();
        final Asset forth = new Asset.Builder("xyz").attribute("first_attr", "first_value")
                .attribute("other_attr", "other_value").type("test").build();
        final Asset fifth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("other").build();
        final Asset sixth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").selfClosing(true).build();
        
        assertIsValueObject(first, second, third, forth, fifth, sixth);
    }
    
    @Test
    public void compositionStep_is_value_object() {
        final CompositionStep first = CompositionStep.root("/").childWith("1", 1, 2);
        final CompositionStep second = CompositionStep.root("/").childWith("1", 1, 2);
        final CompositionStep third = CompositionStep.root("/123");
        final CompositionStep forth = CompositionStep.root("/").childWith("xy", 1, 2);
        final CompositionStep fifth = CompositionStep.root("/123").childWith("1", 1, 2);
        
        assertIsValueObject(first, second, third, forth, fifth);
    }
}
