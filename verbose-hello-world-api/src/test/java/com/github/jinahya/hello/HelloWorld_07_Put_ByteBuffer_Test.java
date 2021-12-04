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
import org.mockito.Mockito;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a {@link NullPointerException} when the
     * {@code buffer} argument is {@code null}.
     */
    @DisplayName("put(null) throws NullPointerException")
    @Test
    void put_ThrowNullPointerException_BufferIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld().put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a {@link BufferOverflowException} when
     * {@link ByteBuffer#remaining() buffer.remaining} is less than {@link HelloWorld#BYTES BYTES}({@value
     * com.github.jinahya.hello.HelloWorld#BYTES}).
     */
    @DisplayName("put(buffer) throws BufferOverflowException when buffer.remaining is less than BYTES")
    @Test
    void put_ThrowBufferOverflowException_BufferRemainingIsNotEnough() {
        final ByteBuffer buffer = Mockito.spy(ByteBuffer.allocate(0));
        final int remaining = new Random().nextInt(HelloWorld.BYTES);
        assert remaining < HelloWorld.BYTES;
        Mockito.when(buffer.remaining())
                .thenReturn(remaining);
        Assertions.assertThrows(BufferOverflowException.class, () -> helloWorld().put(buffer));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a byte buffer which {@link
     * ByteBuffer#hasArray() has a backing array}, invokes {@link HelloWorld#set(byte[], int) set(buffer.array,
     * buffer.arrayOffset + buffer.position)} and increments the {@link ByteBuffer#position(int) buffer.position} by
     * {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer-with-backing-array) invokes set(array, index) and increments position")
    @Test
    void putBuffer_InvokeSetArrayWithIndexAndIncrementPosition_BufferHasBackingArray() {
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[HelloWorld.BYTES]);
        assert buffer.remaining() >= HelloWorld.BYTES;
        assert buffer.hasArray();
        final byte[] array = buffer.array();
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when invoked with a byte buffer without a {@link
     * ByteBuffer#hasArray() backing array}, invokes {@link HelloWorld#set(byte[]) set(array)} method with an array of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and {@link ByteBuffer#put(byte[]) puts} the array to the
     * buffer.
     */
    @DisplayName("put(buffer-with-no-backing-array) invokes set(array) and put the array to the buffer")
    @Test
    void put_InvokeSetArrayPutArrayToBuffer_BufferHasNoBackingArray() {
        final ByteBuffer buffer = Mockito.spy(ByteBuffer.allocateDirect(HelloWorld.BYTES));
        if (buffer.hasArray()) {
            log.info("a direct byte buffer has a backing array");
            Mockito.when(buffer.hasArray())
                    .thenReturn(false);
        }
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method returns given {@code buffer} argument.
     */
    @DisplayName("put(buffer) returns buffer")
    @Test
    void put_ReturnBuffer_() {
        final ByteBuffer expected = ByteBuffer.allocate(HelloWorld.BYTES);
        final ByteBuffer actual = helloWorld().put(expected);
        Assertions.assertSame(expected, actual);
    }
}
