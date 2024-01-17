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
import com.github.jinahya.hello.util.java.nio.ByteBufferUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(buffer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_01_Put_ByteBuffer_Test extends _HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link NullPointerException} when the {@code buffer} argument is {@code null}.
     */
    @DisplayName("""
            should throw aNullPointerException
            when the buffer argument is null"""
    )
    @Test
    void _ThrowNullPointerException_BufferIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = (ByteBuffer) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(buffer)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link BufferOverflowException} when {@code buffer} argument's
     * {@link ByteBuffer#remaining() remaining} is less than
     * {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should throw a BufferOverflowException
            when buffer.remaining() is less than HelloWorld.BYTES"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowBufferOverflowException_BufferRemainingIsLessThanHelloWorldBytes() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        return Stream.of(
                ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)),
                ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES))
        ).map(b -> DynamicTest.dynamicTest(
                "should throw a BufferOverflowException for " + b,
                // ----------------------------------------------------------------------- when/then
                () -> {
                    Assertions.assertThrows(
                            BufferOverflowException.class,
                            () -> service.put(b)
                    );
                }
        ));
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method, invoked with a byte
     * buffer which {@link ByteBuffer#hasArray() has a backing array}, invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with {@code buffer.array} and
     * ({@code buffer.arrayOffset + buffer.position}), and returns the {@code buffer} as its
     * {@link ByteBuffer#position() position} increased by
     * {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should invoke set(buffer.array(), buffer.arrayOffset() + buffer.position())
            when the buffer has array"""
    )
    @Test
    void __BufferHasBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        if (false) {
            BDDMockito.willAnswer(i -> i.getArgument(0, byte[].class))
                    .given(service)
                    .set(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        }
        final ByteBuffer buffer;
        {
            final var b = ByteBuffer.allocate(HelloWorld.BYTES << 1);
            assert b.position() == 0;
            assert b.limit() == b.capacity();
            assert b.hasArray();
            assert b.arrayOffset() == 0;
            final var index = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES + 1);
            final var length = HelloWorld.BYTES + ThreadLocalRandom.current()
                    .nextInt(HelloWorld.BYTES - index + 1);
            buffer = Mockito.spy(b.slice(index, length));
        }
        buffer.position(
                buffer.position() +
                ThreadLocalRandom.current().nextInt(buffer.remaining() - HelloWorld.BYTES + 1)
        );
        buffer.limit(
                buffer.limit() -
                ThreadLocalRandom.current().nextInt(buffer.remaining() - HelloWorld.BYTES + 1)
        );
        ByteBufferUtils.printBuffer(buffer);
        assert buffer.hasArray();
        assert buffer.remaining() >= HelloWorld.BYTES;
        final var position = buffer.position();
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // TODO: verify, set(buffer.array(), buffer.arrayOffset() + <position>) invoked, once
        // TODO: assert, buffer.position() is equal to (<position> + HelloWorld.BYTES)
        Assertions.assertSame(buffer, result);
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method, invoked with a byte
     * buffer which does not {@link ByteBuffer#hasArray() have a backing array}, invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, puts the array to {@code buffer}, and returns the {@code buffer}.
     */
    @DisplayName("""
            -> set(array[HelloWorld.BYTES])
            -> put the array to the buffer"""
    )
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> i.getArgument(0, byte[].class))
                .given(service)
                .set(ArgumentMatchers.any());
        final ByteBuffer buffer;
        {
            final var b = ByteBuffer.allocateDirect(HelloWorld.BYTES << 1);
            assert b.position() == 0;
            assert b.limit() == b.capacity();
            Assumptions.assumeFalse(
                    b.hasArray(),
                    "failed to assume that a direct buffer does not has a backing array"
            );
            b.position(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES + 1));
            b.limit(b.limit() - ThreadLocalRandom.current()
                    .nextInt(b.remaining() - HelloWorld.BYTES + 1));
            buffer = Mockito.spy(b);
        }
        ByteBufferUtils.printBuffer(buffer);
        assert !buffer.hasArray();
        assert buffer.remaining() >= HelloWorld.BYTES;
        final var position = buffer.position();
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        final var arrayCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(service, Mockito.times(1)).set(arrayCaptor.capture());
        final var array = arrayCaptor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        // TODO: verify, buffer.put(array) invoked, once
        Assertions.assertSame(result, buffer);
    }
}
