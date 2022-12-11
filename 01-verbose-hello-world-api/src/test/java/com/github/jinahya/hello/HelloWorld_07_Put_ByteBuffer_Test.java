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

import static java.nio.ByteBuffer.allocate;
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
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the buffer has a backing
     * array, invokes {@link HelloWorld#set(byte[], int) set(array, index)} method with
     * {@code buffer.array} and ({@code buffer.arrayOffset + buffer.position}) and asserts that the
     * {@code buffer.position} is increased by {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer-with-backing-array)"
                 + " invokes set(buffer.array, buffer.arrayOffset + buffer.position)"
                 + " and increases buffer.position by 12")
//    @RepeatedTest(1024)
    @Test
    void put_InvokeSetArrayIndexAndIncrementPosition_BufferHasBackingArray() {
        var service = service();
        ByteBuffer buffer;
        {
            var array = new byte[HelloWorld.BYTES * 3];
            var wrapper = wrap(array);
            assert wrapper.hasArray();
            assert wrapper.array() == array;
            assert wrapper.arrayOffset() == 0;
            assert wrapper.capacity() == array.length;
            assert wrapper.limit() == wrapper.capacity();
            assert wrapper.position() == 0;
            assert wrapper.remaining() == wrapper.capacity();
            buffer = wrapper.slice();
            assert buffer.hasArray();
            wrapper.position(current().nextInt(HelloWorld.BYTES));
            assert buffer.array() == array;
        }
        service.put(buffer);
        // TODO: Verify service invoked set(buffer.array(), buffer.arrayOffset + buffer.position)
        var position = buffer.position();
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a byte
     * buffer which does not have a backing array, invokes {@link HelloWorld#set(byte[]) set(array)}
     * method with an array of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and puts the
     * array to {@code buffer}.
     */
    @DisplayName("put(buffer-with-no-backing-array)"
                 + " invokes set(array[12])"
                 + " and invokes buffer.put(array)")
    @Test
    void put_InvokeSetArrayPutArrayToBuffer_BufferHasNoBackingArray() {
        var buffer = spy(ByteBuffer.allocateDirect(HelloWorld.BYTES));
        assumeFalse(buffer.hasArray(), "a direct buffer has a backing array?");
        var position = buffer.position();
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method returns given {@code buffer}
     * argument.
     */
    @DisplayName("put(buffer) returns buffer")
    @Test
    void put_ReturnBuffer_() {
        var service = service();
        var buffer = allocate(HelloWorld.BYTES);
        var actual = service.put(buffer);
        assertSame(buffer, actual);
    }
}
