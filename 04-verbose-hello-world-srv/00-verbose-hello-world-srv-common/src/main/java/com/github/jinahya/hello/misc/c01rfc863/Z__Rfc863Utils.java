package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

final class Z__Rfc863Utils {

    static Process fork(final Class<?> main) throws IOException {
        Objects.requireNonNull(main, "main is null");
        final String home = System.getProperty("java.home");
        final String java = Paths.get(home, "bin", "java").toString();
        final String path = System.getProperty("java.class.path");
        final var command = List.of(
                java,
                "-cp",
                path,
                main.getName()
        );
        return new ProcessBuilder(command)
                .inheritIO()
                .start();
    }

    private Z__Rfc863Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
