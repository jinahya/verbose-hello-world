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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_Put_ByteBuffer_Arguments_Test
 */
@DisplayName("put(buffer)")
@Slf4j
class HelloWorld_07_Put_ByteBuffer_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to just return the {@code array}
     * argument.
     */
    @DisplayName("[stubbing] set(array) returns array")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0))
                .when(service())
                .set(any());
    }

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
        // GIVEN
        var service = service();
        var array = new byte[BYTES];
        var buffer = mock(ByteBuffer.class);
        when(buffer.remaining()).thenReturn(array.length);
        when(buffer.hasArray()).thenReturn(true);
        when(buffer.array()).thenReturn(array);
        when(buffer.arrayOffset()).thenReturn(0);
        when(buffer.position()).thenReturn(0);
        var arrayOffset = buffer.arrayOffset();
        var position = buffer.position();
        // WHEN
        service.put(buffer);
        // THEN: once, set(array, arrayOffset + position) invoked
        // THEN: once, buffer.position(position + 12) invoked
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
        // GIVEN
        var service = service();
        var buffer = mock(ByteBuffer.class);
        when(buffer.remaining()).thenReturn(BYTES);
        when(buffer.hasArray()).thenReturn(false);
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
        var buffer = mock(ByteBuffer.class);
        when(buffer.remaining()).thenReturn(BYTES);
        // WHEN
        var actual = service.put(buffer);
        // THEN
        assertSame(buffer, actual);
    }
}
