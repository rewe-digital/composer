package com.rewedigital.composer.util.mergable;

import java.util.Optional;

public interface Mergables {
    public <T extends Mergable<?>> Optional<T> get(Class<T> type);
}