package io.github.joaoscioli.fraud.rules;

import java.util.Optional;
import java.util.stream.Stream;

final class OptionalUtils {
    private OptionalUtils() {
    }

    static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}
