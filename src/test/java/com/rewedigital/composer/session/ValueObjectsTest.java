package com.rewedigital.composer.session;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import java.util.HashMap;

import org.junit.Test;

public class ValueObjectsTest {

    @SuppressWarnings("serial")
    @Test
    public void sessionData_is_value_object() {
        SessionData first = new SessionData(new HashMap<String, String>() {
            {
                put("x-rd-first", "value");
            }
        });
        SessionData second = new SessionData(new HashMap<String, String>() {
            {
                put("x-rd-first", "value");
            }
        });

        SessionData third = new SessionData(new HashMap<String, String>() {
            {
                put("x-rd-first", "other");
            }
        });

        assertIsValueObject(first, second, third);
    }

}
