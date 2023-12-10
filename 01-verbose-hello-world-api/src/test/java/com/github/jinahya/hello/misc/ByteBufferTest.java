package com.github.jinahya.hello.misc;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.util.java.nio.JavaNioUtils.print;
import static java.nio.ByteBuffer.allocateDirect;

@Slf4j
class ByteBufferTest {

    private static void debugArray(final String name, final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        log.debug("{}: ({})({}){}", name,
                  String.format("%1$016x", Arrays.hashCode(array)),
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
        var buffer = allocateDirect(capacity);
        debugBuffer(buffer);
        assert buffer.capacity() == capacity;
        assert buffer.limit() == buffer.capacity();
        assert buffer.position() == 0;
        while (buffer.hasRemaining()) {
            assert buffer.get() == 0;
        }
    }

    @Test
    void printDocumented() {
        var buffer = allocateDirect(31).position(4).limit(25);
        print(buffer);
        buffer.position(buffer.position() + BYTES);
        assert buffer.position() == 16;
        print(buffer);
    }
}
