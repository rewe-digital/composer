package com.rewedigital.composer.composing;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import org.junit.Test;

import com.rewedigital.composer.html.ContentRange;

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
    public void compositionStep_is_value_object() {
        final CompositionStep first = CompositionStep.root("/").childWith("1", 1, 2);
        final CompositionStep second = CompositionStep.root("/").childWith("1", 1, 2);
        final CompositionStep third = CompositionStep.root("/123");
        final CompositionStep forth = CompositionStep.root("/").childWith("xy", 1, 2);
        final CompositionStep fifth = CompositionStep.root("/123").childWith("1", 1, 2);
        
        assertIsValueObject(first, second, third, forth, fifth);
    }
}
