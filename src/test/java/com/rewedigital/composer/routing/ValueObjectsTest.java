package com.rewedigital.composer.routing;

import static com.rewedigital.composer.helper.ValueObjectAssertions.assertIsValueObject;

import java.time.Duration;

import org.junit.Test;

public class ValueObjectsTest {

    @Test
    public void match_is_a_value_object() {
        Match first = Match.of("test", Duration.ofMillis(1000), RouteTypeName.PROXY);
        Match second = Match.of("test", Duration.ofMillis(1000), RouteTypeName.PROXY);
        Match third = Match.of("test", RouteTypeName.PROXY);
        Match forth = Match.of("other", Duration.ofMillis(1000), RouteTypeName.PROXY);
        Match fifth = Match.of("test", Duration.ofMillis(1000), RouteTypeName.TEMPLATE);

        assertIsValueObject(first, second, third, forth, fifth);
    }

}
