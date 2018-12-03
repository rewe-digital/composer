package com.rewedigital.composer.caching;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rewedigital.composer.caching.CacheControlComposer;
import com.squareup.okhttp.CacheControl;

public class CacheControlComposerTest {

    @Test
    public void composesEmptyCacheControlToBeNotCacheable() {
        final CacheControlComposer composer = new CacheControlComposer();

        final CacheControl result = composer.build();

        assertThat(result)
            .isEqualToComparingFieldByField(new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build());
    }

    @Test
    public void composesSingleCacheControlMaxAge() {
        final CacheControlComposer composer =
            new CacheControlComposer()
                .composeWith(new CacheControl.Builder().maxAge(100, TimeUnit.SECONDS).build());

        final CacheControl result = composer.build();
        assertThat(result)
            .isEqualToComparingFieldByField(new CacheControl.Builder()
                .maxAge(100, TimeUnit.SECONDS).build());
    }

    @Test
    public void composesMultipleMaxAgesIntoMinimum() {
        final CacheControlComposer composer = new CacheControlComposer()
            .composeWith(new CacheControl.Builder().maxAge(50, TimeUnit.SECONDS).build())
            .composeWith(new CacheControl.Builder().maxAge(100, TimeUnit.SECONDS).build());

        final CacheControl result = composer.build();
        assertThat(result)
            .isEqualToComparingFieldByField(new CacheControl.Builder()
                .maxAge(50, TimeUnit.SECONDS).build());
    }

}
