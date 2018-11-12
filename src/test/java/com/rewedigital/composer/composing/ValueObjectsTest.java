package com.rewedigital.composer.composing;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import java.time.Duration;
import java.util.Optional;

import org.junit.Test;

public class ValueObjectsTest {

    @Test
    public void contentRange_is_value_object() {
        ContentRange first = new ContentRange(0, 10);
        ContentRange second = new ContentRange(0, 10);
        ContentRange third = new ContentRange(10, 20);
        ContentRange forth = new ContentRange(5, 10);
        ContentRange fifth = new ContentRange(0, 20);

        assertIsValueObject(first, second, third, forth, fifth);
    }

    @Test
    public void fetchContext_is_value_object() {
        FetchContext first = FetchContext.of("/", "/fallback", Optional.empty());
        FetchContext second = FetchContext.of("/", "/fallback", Optional.empty());
        FetchContext third = FetchContext.of("/test", "/fallback", Optional.empty());
        FetchContext forth = FetchContext.of("/", "/other", Optional.empty());
        FetchContext fifth = FetchContext.of("/", "/fallback", Optional.of(Duration.ofMillis(1000)));

        assertIsValueObject(first, second, third, forth, fifth);
    }

    @Test
    public void asset_is_value_object() {
        Asset first = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        Asset second = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        Asset third = new Asset.Builder("xyz").attribute("third_attr", "first_value").type("test").build();
        Asset forth = new Asset.Builder("xyz").attribute("first_attr", "first_value")
                .attribute("other_attr", "other_value").type("test").build();
        Asset fifth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("other").build();
        Asset sixth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").selfClosing(true).build();
        
        assertIsValueObject(first, second, third, forth, fifth, sixth);
    }
    
    @Test
    public void compositionStep_is_value_object() {
        CompositionStep first = CompositionStep.root("/").childWith("1");
        CompositionStep second = CompositionStep.root("/").childWith("1");
        CompositionStep third = CompositionStep.root("/123");
        CompositionStep forth = CompositionStep.root("/").childWith("xy");
        CompositionStep fifth = CompositionStep.root("/123").childWith("1");
        
        assertIsValueObject(first, second, third, forth, fifth);
    }
}
