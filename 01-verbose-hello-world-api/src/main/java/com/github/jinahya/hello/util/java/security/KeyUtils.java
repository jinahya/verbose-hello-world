package com.github.jinahya.hello.util.java.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.spec.EncodedKeySpec;
import java.util.Objects;
import java.util.function.Function;

public final class KeyUtils {

    public static void writeEncoded(
            final Key key, final Function<? super byte[], ? extends EncodedKeySpec> specFunction,
            final Path path)
            throws IOException {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(specFunction, "specFunction is null");
        Objects.requireNonNull(path, "path is null");
        final var encoded = key.getEncoded(); // nullable
        final var keySpec = specFunction.apply(encoded);
        final var encodedKey = keySpec.getEncoded();
        Files.write(path, encodedKey);
    }

    public static <K extends Key> K readEncoded(
            final Path path,
            final Function<? super byte[], ? extends EncodedKeySpec> specFunction,
            final Function<? super EncodedKeySpec, ? extends K> keyFunction)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(specFunction, "specFunction is null");
        Objects.requireNonNull(keyFunction, "keyFunction is null");
        final var encodedKey = Files.readAllBytes(path);
        final var keySpec = specFunction.apply(encodedKey);
        return keyFunction.apply(keySpec);
    }

    private KeyUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
