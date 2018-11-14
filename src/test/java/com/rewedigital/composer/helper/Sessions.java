package com.rewedigital.composer.helper;

import java.util.HashMap;
import java.util.Map;

import com.rewedigital.composer.session.SessionRoot;
import com.spotify.apollo.Response;

public class Sessions {

    private static final SessionRoot.Serializer noop = new SessionRoot.Serializer() {
        @Override
        public <T> Response<T> writeTo(Response<T> response, Map<String, String> sessionData, boolean dirty) {
            return response;
        }
    };

    public static SessionRoot cleanSessionRoot(final String key, final String value) {
        return sessionRoot(key, value, false);
    }

    public static SessionRoot cleanSessionRoot(final String key, final String value,
        final SessionRoot.Serializer serializer) {
        return sessionRoot(key, value, false, serializer);
    }

    public static SessionRoot dirtySessionRoot(final String key, final String value) {
        return sessionRoot(key, value, true);
    }

    public static SessionRoot dirtySessionRoot(final String key, final String value,
        final SessionRoot.Serializer serializer) {
        return sessionRoot(key, value, true, serializer);
    }


    public static SessionRoot sessionRoot(final String key, final String value) {
        return sessionRoot(key, value, false);
    }

    public static SessionRoot sessionRoot(final String key, final String value, final boolean dirty) {
        return sessionRoot(key, value, dirty, noop);
    }

    public static SessionRoot sessionRoot(final String key, final String value, final boolean dirty,
        final SessionRoot.Serializer serializer) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(key, value);
        return SessionRoot.of(serializer, data, dirty);
    }

    public static SessionRoot sessionRootExpiringAt(final long epochSeconds, final String key, final String value) {
        return sessionRootExpiringAt(Long.toString(epochSeconds), key, value);
    }

    public static SessionRoot sessionRootExpiringAt(final String expireAt, final String key, final String value) {
        final Map<String, String> data = new HashMap<>();
        data.put("x-rd-session-id", "1234");
        data.put("expires-at", expireAt);
        data.put(key, value);
        return SessionRoot.of(noop, data);
    }
}
