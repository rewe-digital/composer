package com.rewedigital.composer.caching;

import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.CacheControl;

public class CacheControlComposer {

    private int maxAgeSeconds = -1;

    public CacheControl build() {
        final CacheControl.Builder result = new CacheControl.Builder();
        result.maxAge(maxAgeSeconds >= 0 ? maxAgeSeconds : 0, TimeUnit.SECONDS);
        return result.build();
    }

    public CacheControlComposer composeWith(final CacheControl cacheControl) {
        maxAge(cacheControl.maxAgeSeconds());
        return this;
    }

    //
    private void maxAge(final int maxAge) {
        if (maxAge >= 0 && (this.maxAgeSeconds < 0 || maxAge < this.maxAgeSeconds)) {
            this.maxAgeSeconds = maxAge;
        }
    }

}
