package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
class JavaNioByteBufferUtilsTest {

    static Stream<ByteBuffer> randomByteBufferStream() {
        return Stream.concat(
                JavaLangArrayUtilsTest.randomByteArrayStream().map(ByteBuffer::wrap),
                IntStream.rangeClosed(0, ThreadLocalRandom.current().nextInt(8))
                        .mapToObj(i -> ByteBuffer.allocateDirect(
                                ThreadLocalRandom.current().nextInt(8)))
        ).peek(b -> {
            b.limit(ThreadLocalRandom.current().nextInt(b.capacity() + 1))
                    .position(ThreadLocalRandom.current().nextInt(b.remaining() + 1));
        });
    }

    @DisplayName("randomize(buffer)")
    @Nested
    class RandomizeTest {

        @MethodSource({
                "com.github.jinahya.hello.util.JavaNioByteBufferUtilsTest#randomByteBufferStream"
        })
        @ParameterizedTest
        void __(final ByteBuffer buffer) {
            // ------------------------------------------------------------------------------- given
            final var position = buffer.position();
            final var limit = buffer.limit();
            // -------------------------------------------------------------------------------- when
            final var result = JavaNioByteBufferUtils.randomize(buffer);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(buffer, result);
            Assertions.assertEquals(position, result.position());
            Assertions.assertEquals(limit, result.limit());
        }
    }
}
