package com.github.jinahya.hello.c04nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.util.JavaNioUtils.print;
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
 * @see HelloWorld_31_Put_ByteBuffer_Arguments_Test
 */
@DisplayName("put(buffer)")
@Slf4j
class HelloWorld_31_Put_ByteBuffer_Test extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_SetArray_ToReturnTheArray();
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the {@code buffer} has a
     * backing array, invokes {@link HelloWorld#set(byte[], int) set(array, index)} method with
     * {@code buffer.array} and ({@code buffer.arrayOffset + buffer.position}), and returns the
     * {@code buffer} as its {@link ByteBuffer#position() position} increased by
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName(
            "(buffer.hasArray()) -> set(buffer.array, buffer.arrayOffset + buffer.position)")
    @Test
    void __BufferHasBackingArray() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        ByteBuffer buffer;
        {
            var capacity = BYTES + (BYTES << 1); // BYTES * 3
            var whole = current().nextBoolean() ? wrap(new byte[capacity]) : allocate(capacity);
            assert whole.hasArray();
            var index = current().nextInt(BYTES >> 1);
            var length = capacity - index - current().nextInt(BYTES >> 1);
            buffer = spy(whole.slice(index, length));
            assert buffer.hasArray();
            buffer.position(current().nextInt(BYTES >> 1));
            buffer.limit(buffer.capacity() - current().nextInt(BYTES >> 1));
        }
        print(buffer);
        assert buffer.remaining() >= BYTES;
        var position = buffer.position();
        // ------------------------------------------------------------------------------------ when
        var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify, once, set(buffer.array, buffer.arrayOffset + buffer.position) invoked
        // TODO: Assert, buffer.position is equal to (position + 12).
        assertSame(buffer, result);
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a byte
     * buffer which does not have a backing array, invokes {@link HelloWorld#set(byte[]) set(array)}
     * method with an array of {@value HelloWorld#BYTES} bytes, puts the array to {@code buffer},
     * and returns the {@code buffer}.
     */
    @DisplayName("(!buffer.hasArray()) -> buffer.put(set(array[12]))")
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var buffer = spy(allocateDirect(BYTES + (BYTES << 1))); // BYTES * 3
        buffer.position(current().nextInt(BYTES));
        buffer.limit(buffer.capacity() - current().nextInt(BYTES));
        assert buffer.remaining() >= BYTES;
        print(buffer);
        var position = buffer.position();
        assumeFalse(buffer.hasArray(), "a direct buffer hase a backing array, aborting...");
        // ------------------------------------------------------------------------------------ when
        var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify, once, set(array[12]) invoked
        // TODO: Verify, once, buffer.put(array) invoked
        assertSame(result, buffer);
    }
}
