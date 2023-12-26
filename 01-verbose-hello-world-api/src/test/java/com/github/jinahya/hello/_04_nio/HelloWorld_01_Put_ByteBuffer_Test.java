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
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Put_ByteBuffer_Arguments_Test
 */
@DisplayName("put(buffer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_01_Put_ByteBuffer_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        setArray_willReturnArray();
    }

    /**
     * Verifies {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the {@code buffer} has a
     * backing array, invokes {@link HelloWorld#set(byte[], int) set(array, index)} method with
     * {@code buffer.array} and ({@code buffer.arrayOffset + buffer.position}), and returns the
     * {@code buffer} as its {@link ByteBuffer#position() position} increased by
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName("""
            [buffer.hasArray]
            -> set(buffer.array(), buffer.arrayOffset() + buffer.position())
            -> buffer.position is increased by HelloWorld.BYTES
            """
    )
    @Test
    void __BufferHasBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final ByteBuffer buffer;
        {
            final var b = ByteBuffer.allocate(HelloWorld.BYTES << 1);
            assert b.position() == 0;
            assert b.limit() == b.capacity();
            assert b.hasArray();
            assert b.arrayOffset() == 0;
            final var index = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES + 1);
            final var length = HelloWorld.BYTES +
                               ThreadLocalRandom.current().nextInt(HelloWorld.BYTES - index + 1);
            buffer = b.slice(index, length);
        }
        buffer.position(
                buffer.position() +
                ThreadLocalRandom.current().nextInt(buffer.remaining() - HelloWorld.BYTES + 1)
        );
        buffer.limit(
                buffer.limit() -
                ThreadLocalRandom.current().nextInt(buffer.remaining() - HelloWorld.BYTES + 1)
        );
        JavaNioUtils.print(buffer);
        assert buffer.hasArray();
        assert buffer.remaining() >= HelloWorld.BYTES;
        final var position = buffer.position();
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // TODO: verify, set(buffer.array(), buffer.arrayOffset() + buffer.position()) invoked, once
        // TODO: assert, buffer.position() is equal to (<position> + HelloWorld.BYTES)
        Assertions.assertSame(buffer, result);
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a byte
     * buffer which does not have a backing array, invokes {@link HelloWorld#set(byte[]) set(array)}
     * method with an array of {@value HelloWorld#BYTES} bytes, puts the array to {@code buffer},
     * and returns the {@code buffer}.
     */
    @DisplayName("""
            [!buffer.hasArray]
            -> set(array[HelloWorld.BYTES])
            -> buffer.put(array)
            """
    )
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final ByteBuffer buffer;
        {
            final var b = ByteBuffer.allocateDirect(HelloWorld.BYTES << 1);
            assert b.position() == 0;
            assert b.limit() == b.capacity();
            Assumptions.assumeFalse(
                    b.hasArray(),
                    "failed to assume that a direct buffer does not has a backing array"
            );
            b.position(
                    ThreadLocalRandom.current().nextInt(HelloWorld.BYTES + 1)
            );
            b.limit(
                    b.limit() -
                    ThreadLocalRandom.current().nextInt(b.remaining() - HelloWorld.BYTES + 1)
            );
            JavaNioUtils.print(b);
            buffer = Mockito.spy(b);
        }
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).set(arrayCaptor().capture());
        final var array = arrayCaptor().getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        // TODO: verify, buffer.put(<array>) invoked, once
        Assertions.assertSame(result, buffer);
    }
}
