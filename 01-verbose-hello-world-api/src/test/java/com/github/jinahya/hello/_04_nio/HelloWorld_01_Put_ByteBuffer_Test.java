package com.github.jinahya.hello._04_nio;

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

import static com.github.jinahya.hello.util.java.nio.JavaNioUtils.print;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Put_ByteBuffer_Arguments_Test
 */
@DisplayName("put(buffer)")
@Slf4j
class HelloWorld_01_Put_ByteBuffer_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        setArray_willReturnTheArray();
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the {@code buffer} has a
     * backing array, invokes {@link HelloWorld#set(byte[], int) set(array, index)} method with
     * {@code buffer.array} and ({@code buffer.arrayOffset + buffer.position}), and returns the
     * {@code buffer} as its {@link ByteBuffer#position() position} increased by
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName("""
            buffer.hasArray -> set(buffer.array, buffer.arrayOffset + buffer.position)
            """
    )
    @Test
    void __BufferHasBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
        print(buffer);
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
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
    @DisplayName(
            """
                    !buffer.hasArray -> buffer.put(set(array[12]))
                    """
    )
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = ByteBuffer.allocateDirect(HelloWorld.BYTES);
        print(buffer);
        assumeFalse(buffer.hasArray(),
                    "failed to assume that a direct buffer does not has a backing array");
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify, once, set(array[12]) invoked
        // TODO: Verify, once, buffer.put(array) invoked
        assertSame(result, buffer);
    }
}
