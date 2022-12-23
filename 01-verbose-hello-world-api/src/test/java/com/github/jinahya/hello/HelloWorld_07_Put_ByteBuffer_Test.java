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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteBuffer.wrap;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Mockito.spy;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_Put_ByteBuffer_Arguments_Test
 */
@DisplayName("put(ByteBuffer)")
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the {@code buffer} has a
     * backing array, invokes {@link HelloWorld#set(byte[], int) set(array, index)} method with
     * {@code buffer.array} and ({@code buffer.arrayOffset + buffer.position}), and asserts that the
     * {@code buffer.position} is increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[.hasArray()]"
                 + " -> set(.array, .arrayOffset + .position)"
                 + " -> .position += 12")
    @Test
    void _InvokeSetArrayIndexAndIncrementPosition_BufferHasBackingArray() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: ByteBuffer
        var buffer = ByteBuffer.allocate(BYTES + current().nextInt(BYTES));
        if (buffer.remaining() > BYTES) {
            buffer.position(current().nextInt(buffer.remaining() - BYTES));
        }
        if (current().nextBoolean()) {
            var array = new byte[BYTES * 3];
            var wrapper = wrap(array);
            assert wrapper.hasArray();
            assert wrapper.array() == array;
            assert wrapper.arrayOffset() == 0;
            assert wrapper.capacity() == array.length;
            assert wrapper.limit() == wrapper.capacity();
            assert wrapper.position() == 0;
            assert wrapper.remaining() == wrapper.limit();
            var index = current().nextInt(BYTES);
            var length = current().nextInt(BYTES) + BYTES;
            buffer = wrapper.slice(index, length);
            assert buffer.array() == array;
            assert buffer.hasArray();
            assert buffer.arrayOffset() == index;
            assert buffer.position() == 0;
            if (buffer.remaining() > BYTES) {
                buffer.position(current().nextInt(buffer.remaining() - BYTES));
            }
        }
        assert buffer.hasArray();
        assert buffer.remaining() >= BYTES;
        var array = buffer.array();
        var arrayOffset = buffer.arrayOffset();
        var position = buffer.position();
        // WHEN
        service.put(buffer);
        // THEN: once, set(buffer.array, buffer.arrayOffset + buffer.position) invoked
        // THEN: buffer.position increased by 12
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a byte
     * buffer which does not have a backing array, invokes {@link HelloWorld#set(byte[]) set(array)}
     * method with an array of {@value HelloWorld#BYTES} bytes, and puts the array to
     * {@code buffer}.
     */
    @DisplayName("[!.hasArray()]"
                 + " -> set(array[12])"
                 + " -> .put(array)")
    @Test
    void _InvokeSetArrayPutArrayToBuffer_BufferHasNoBackingArray() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: ByteBuffer
        var capacity = current().nextInt(BYTES) + BYTES;
        var buffer = spy(allocateDirect(capacity));
        assumeFalse(buffer.hasArray(), "a direct buffer has a backing array");
        if (buffer.limit() > BYTES) {
            buffer.position(current().nextInt(buffer.limit() - BYTES));
        }
        assert buffer.remaining() >= BYTES;
        assert !buffer.hasArray();
        var position = buffer.position();
        // WHEN
        service.put(buffer);
        // THEN: once, set(array[12]) invoked
        // THEN: once, buffer.put(array) invoked
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method returns given {@code buffer}
     * argument.
     */
    @DisplayName("returns buffer")
    @Test
    void _ReturnBuffer_() {
        // GIVEN
        var service = service();
        var buffer = allocate(BYTES);
        // WHEN
        var actual = service.put(buffer);
        // THEN
        assertSame(buffer, actual);
    }
}
