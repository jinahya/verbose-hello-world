package com.github.jinahya.hello.misc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
class ByteBufferTest {

    private static void debugArray(final String name, final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        log.debug("{}: ({})({}){}", name, String.format("%1$016x", Arrays.hashCode(array)),
                  array.length, array);
    }

    private static void debugBuffer(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        log.debug("buffer.capacity: {}", buffer.capacity());
        log.debug("buffer.limit: {}", buffer.limit());
        log.debug("buffer.position: {}", buffer.position());
        log.debug("buffer.remaining: {}", buffer.remaining());
        log.debug("buffer.hasRemaining: {}", buffer.hasRemaining());
        log.debug("buffer.hasArray: {}", buffer.hasArray());
        if (buffer.hasArray()) {
            debugArray("buffer.array", buffer.array());
            log.debug("buffer.arrayOffset: {}", buffer.arrayOffset());
        }
    }

    private static IntStream arrayLengths() {
        return IntStream.of(0, 1, 2, 4);
    }

    /**
     * Tests {@link ByteBuffer#wrap(byte[])} method with an array of specified length.
     *
     * @param length the length of the array.
     */
    @MethodSource({"arrayLengths"})
    @ParameterizedTest(name = "Buffer.wrap(byte[{0}])")
    void wrap__(final int length) {
        var array = new byte[length];
        debugArray("array", array);
        var buffer = ByteBuffer.wrap(array);
        debugBuffer(buffer);
        assert buffer.hasArray();
        assert buffer.array() == array;
        assert buffer.arrayOffset() == 0;
        assert buffer.capacity() == buffer.array().length;
        assert buffer.limit() == buffer.array().length;
        assert buffer.position() == 0;
        assert buffer.remaining() == array.length;
    }

    private static IntStream capacities() {
        return arrayLengths();
    }

    @MethodSource({"capacities"})
    @ParameterizedTest(name = "Buffer.allocate({0})")
    void allocate__(final int capacity) {
        var buffer = ByteBuffer.allocate(capacity);
        debugBuffer(buffer);
        assert buffer.capacity() == capacity;
        assert buffer.limit() == buffer.capacity();
        assert buffer.position() == 0;
        assert buffer.hasArray();
        assert buffer.arrayOffset() == 0;
        while (buffer.hasRemaining()) {
            assert buffer.get() == 0;
        }
    }

    @MethodSource({"capacities"})
    @ParameterizedTest(name = "Buffer.allocateDirect({0})")
    void allocateDirect__(final int capacity) {
        var buffer = ByteBuffer.allocateDirect(capacity);
        debugBuffer(buffer);
        assert buffer.capacity() == capacity;
        assert buffer.limit() == buffer.capacity();
        assert buffer.position() == 0;
        while (buffer.hasRemaining()) {
            assert buffer.get() == 0;
        }
    }
}