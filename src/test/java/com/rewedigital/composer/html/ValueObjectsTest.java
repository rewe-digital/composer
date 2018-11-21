package com.rewedigital.composer.html;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import org.junit.Test;

public class ValueObjectsTest {

    @Test
    public void asset_is_value_object() {
        final Asset first = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        final Asset second = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test").build();
        final Asset third = new Asset.Builder("xyz").attribute("third_attr", "first_value").type("test").build();
        final Asset forth = new Asset.Builder("xyz").attribute("first_attr", "first_value")
                .attribute("other_attr", "other_value").type("test").build();
        final Asset fifth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("other").build();
        final Asset sixth = new Asset.Builder("xyz").attribute("first_attr", "first_value").type("test")
                .selfClosing(true).build();

        assertIsValueObject(first, second, third, forth, fifth, sixth);
    }

}
