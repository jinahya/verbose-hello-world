package com.github.jinahya.hello._02_io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

@Slf4j
class MockTest {

    @Test
    void __String() {
        try (var construction = Mockito.mockConstruction(String.class, (m, c) -> {
        })) {
            var s = new String("aaa");
            var s2 = new String();
            log.debug("size: {}", construction.constructed().size());
        }
    }

    @Test
    void __File() {
        try (var construction = Mockito.mockConstruction(File.class, (m, c) -> {
        })) {
            var s = new File("aaa");
            log.debug("size: {}", construction.constructed().size());
        }
        final var bytes = new byte[8];
        final var builder = new StringBuilder();
        for (final var b : bytes) {
            builder.append((char) b);
        }
    }
}
