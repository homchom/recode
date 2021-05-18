package io.github.codeutilities.config.structure;

import java.util.Optional;

public interface IRawKey<T> {
    default T setKeyName(String key) {
        return null;
    }

    default Optional<String> getKeyName() {
        return Optional.empty();
    }
}
