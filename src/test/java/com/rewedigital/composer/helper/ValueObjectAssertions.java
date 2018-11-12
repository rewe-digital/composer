package com.rewedigital.composer.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

public final class ValueObjectAssertions {

    @SafeVarargs
    public static <T> void assertIsValueObject(T first, T second, T... others) {
        assertEqualObjectsAreEqual(first, second);
        assertUnequalObjectsAreNotEqual(second, others);
        assertEqualObjectsHaveEqualHashCode(first, second);
        assertToStringOverwritten(first);
    }

    private static void assertEqualObjectsAreEqual(Object first, Object second) {
        assertEquals(first, first);
        assertEquals(first, second);
        assertNotSame(first, second);
    }

    private static void assertUnequalObjectsAreNotEqual(Object second, Object... others) {
        assertNotEquals(second, null);
        assertNotEquals(second, new Object() {
        });

        for (Object other : others) {
            assertNotEquals(second, other);
        }
    }

    private static void assertEqualObjectsHaveEqualHashCode(Object first, Object second) {
        assertEquals(first.hashCode(), first.hashCode());
        assertEquals(first.hashCode(), second.hashCode());
    }

    private static void assertToStringOverwritten(Object obj) {
        assertFalse(obj.toString().startsWith("java.lang.Object@"));
    }
}
