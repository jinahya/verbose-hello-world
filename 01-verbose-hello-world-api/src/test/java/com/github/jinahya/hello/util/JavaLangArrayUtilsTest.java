package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
class JavaLangArrayUtilsTest {

    static Stream<byte[]> randomByteArrayStream() {
        return IntStream.rangeClosed(0, ThreadLocalRandom.current().nextInt(8))
                .mapToObj(i -> new byte[ThreadLocalRandom.current().nextInt(8)])
                ;
    }

    @Nested
    class RandomizeTest {

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void __(final byte[] array) {
            // ------------------------------------------------------------------------------- given
            final var offset = ThreadLocalRandom.current().nextInt(array.length + 1);
            final var length = ThreadLocalRandom.current().nextInt(array.length - offset + 1);
            // -------------------------------------------------------------------------------- when
            JavaLangArrayUtils.randomize(array, offset, length);
            log.debug("array: {}, offset: {}, length: {}", array, offset, length);
            // -------------------------------------------------------------------------------- then
            for (int i = 0; i < offset; i++) {
                Assertions.assertEquals(0, array[i]);
            }
            for (int i = offset + length; i < array.length; i++) {
                Assertions.assertEquals(0, array[i]);
            }
        }
    }
}
