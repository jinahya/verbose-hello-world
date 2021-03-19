package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
class HelloWorld_PutBufferTest extends AbstractHelloWorldTest {

    /**
     * Returns a stream of byte buffers which each remaining is less than {@link HelloWorld#BYTES}.
     *
     * @return a stream of byte buffers which each remaining is less than {@link HelloWorld#BYTES}.
     */
    private static Stream<ByteBuffer> buffersOfNotEnoughRemaining() {
        return Stream
                .of(ByteBuffer.wrap(new byte[current().nextInt(HelloWorld.BYTES)]),
                    ByteBuffer.allocate(current().nextInt(HelloWorld.BYTES)),
                    ByteBuffer.allocateDirect(current().nextInt(HelloWorld.BYTES)))
                .peek(b -> {
                    Assertions.assertTrue(b.remaining() < HelloWorld.BYTES);
                });
    }

    /**
     * Returns a stream of byte buffers which each has a backing array.
     *
     * @return a stream of byte buffers which each has a backing array.
     */
    private static Stream<ByteBuffer> buffersHasBackingArray() {
        return IntStream.range(0, 8)
                .mapToObj(i -> {
                    if (current().nextBoolean()) {
                        final byte[] array = new byte[HelloWorld.BYTES * 3];
                        final int offset = current().nextInt(HelloWorld.BYTES);
                        final int length = current().nextInt(HelloWorld.BYTES, array.length - offset);
                        return ByteBuffer.wrap(array, offset, length);
                    } else {
                        final ByteBuffer buffer = ByteBuffer.allocate(HelloWorld.BYTES * 3);
                        Assertions.assertEquals(0, buffer.position());
                        Assertions.assertEquals(buffer.capacity(), buffer.limit());
                        buffer.position(current().nextInt(HelloWorld.BYTES));
                        buffer.limit(current().nextInt(buffer.position() + HelloWorld.BYTES, buffer.limit()));
                        return buffer;
                    }
                })
                .peek(b -> {
                    Assertions.assertTrue(b.remaining() >= HelloWorld.BYTES);
                    Assertions.assertTrue(b.hasArray());
                });
    }

    /**
     * Returns a stream of byte buffers which each has no backing array.
     *
     * @return a stream of byte buffers which each has no backing array.
     */
    private static Stream<ByteBuffer> buffersHasNoBackingArray() {
        return IntStream.range(0, 8)
                .mapToObj(i -> {
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(HelloWorld.BYTES * 3);
                    Assertions.assertEquals(0, buffer.position());
                    Assertions.assertEquals(buffer.capacity(), buffer.limit());
                    Assertions.assertTrue(buffer.remaining() >= HelloWorld.BYTES);
                    buffer.position(current().nextInt(HelloWorld.BYTES));
                    buffer.limit(current().nextInt(buffer.position() + HelloWorld.BYTES, buffer.limit()));
                    return buffer;
                })
                .peek(b -> {
                    Assertions.assertTrue(b.remaining() >= HelloWorld.BYTES);
                    Assertions.assertFalse(b.hasArray());
                });
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @DisplayName("put(buffer) throws NullPointerException when buffer is null")
    @Test
    void putBuffer_NullPointerException_BufferIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link BufferOverflowException} when {@link
     * ByteBuffer#remaining() buffer.remaining} is less than {@link HelloWorld#BYTES}({@value
     * com.github.jinahya.hello.HelloWorld#BYTES}).
     */
    @DisplayName("put(buffer) throws BufferOverflowException when buffer.remaining is less than BYTES")
    @MethodSource({"buffersOfNotEnoughRemaining"})
    @ParameterizedTest
    void putBuffer_BufferOverflowException_BufferRemainingIsNotEnough(final ByteBuffer buffer) {
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when invoked with a byte buffer with a {@link
     * ByteBuffer#hasArray() backing array}, invokes {@link HelloWorld#set(byte[], int) set(buffer.array,
     * buffer.arrayOffset + buffer.position)} and increments the {@link ByteBuffer#position(int) buffer.position} by
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer-with-backing-array) invokes set(buffer.array, buffer.arrayOffset + buffer.position)")
    @MethodSource({"buffersHasBackingArray"})
    @ParameterizedTest
    void putBuffer_InvokeSetArrayWithIndexAndIncrementPosition_BufferHasBackingArray(ByteBuffer buffer) {
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when invoked with a byte buffer without a {@link
     * ByteBuffer#hasArray() backing array}, invokes {@link HelloWorld#set(byte[])} method with an array of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and {@link ByteBuffer#put(byte[]) puts} the array to the
     * buffer.
     */
    @DisplayName("put(buffer-with-no-backing-array) invokes set(array) and put the array to the buffer")
    @MethodSource({"buffersHasNoBackingArray"})
    @ParameterizedTest
    void putBuffer_InvokeSetArrayPutArrayToBuffer_BufferHasNoBackingArray(ByteBuffer buffer) {
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns given buffer when the buffer has a backing array.
     */
    @DisplayName("put(buffer-with-backing-array) returns buffer")
    @MethodSource({"buffersHasBackingArray"})
    @ParameterizedTest
    void putBuffer_ReturnBuffer_BufferHasBackingArray(final ByteBuffer expected) {
        final ByteBuffer actual = helloWorld.put(expected);
        Assertions.assertSame(expected, actual);
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns given buffer when the buffer has no backing array.
     */
    @DisplayName("put(buffer with no backing array) returns specified buffer")
    @MethodSource({"buffersHasBackingArray"})
    @ParameterizedTest
    void putBuffer_ReturnBuffer_BufferHasNoBackingArray(final ByteBuffer expected) {
        final ByteBuffer actual = helloWorld.put(expected);
        Assertions.assertSame(expected, actual);
    }
}
