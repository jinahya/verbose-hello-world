package com.github.jinahya.hello._04_java_nio;

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
import com.github.jinahya.hello.HelloWorldTest;
import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;
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
class HelloWorld_01_Put_ByteBuffer_Test extends HelloWorldTest {

    private static <R> R adjust(
            final ByteBuffer buffer,
            final Function<
                    ? super ByteBuffer,
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends R>>> function) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < HelloWorld.BYTES) {
            throw new IllegalArgumentException(
                    "buffer.remaining(" + buffer.remaining() + ") < " + HelloWorld.BYTES
            );
        }
        Objects.requireNonNull(function, "function is null");
        final var forwardPosition = ThreadLocalRandom.current().nextInt(
                buffer.remaining() - HelloWorld.BYTES + 1
        );
        final var backwardLimit = ThreadLocalRandom.current().nextInt(
                buffer.remaining() - HelloWorld.BYTES - forwardPosition + 1
        );
        assert (buffer.limit() - backwardLimit - forwardPosition) >= HelloWorld.BYTES;
        return function.apply(buffer).apply(forwardPosition).apply(backwardLimit);
    }

    private static ByteBuffer adjust(final ByteBuffer buffer) {
        return adjust(
                buffer,
                b -> fp -> bl -> b.position(b.position() + fp).limit(b.limit() - bl)
        );
    }

    // ---------------------------------------------------------------------------------------------

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
        // assert, <service.put(buffer)> throws a NullPointerException
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
        // ------------------------------------------------------------------------------- when/then
        return Stream.of(
                ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)),
                ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES))
        ).map(b -> DynamicTest.dynamicTest(
                "should throw a BufferOverflowException for " + b,
                () -> {
                    // assert, service.put(b) throws a BufferOverflowException
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
        if (false) { // not required actually
            BDDMockito.willAnswer(i -> i.getArgument(0, byte[].class))
                    .given(service)
                    .set(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        }
        final ByteBuffer buffer = adjust(
                ByteBuffer.allocate(HelloWorld.BYTES << 1),
                b -> p -> r -> b.slice(p, b.limit() - p - r)
        );
        JavaNioByteBufferUtils.print(buffer);
        assert buffer.hasArray();
        assert buffer.remaining() >= HelloWorld.BYTES;
        final var position = buffer.position();
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.set(buffer.array(), buffer.arrayOffset() + position)> invoked, once

        // assert, <buffer>'s <position> increased by <HelloWorld.BYTES>
        JavaNioByteBufferUtils.print(buffer);

        // assert, <result> is same as <buffer>
        Assertions.assertSame(buffer, result);
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method, invoked with a byte
     * buffer which does not {@link ByteBuffer#hasArray() have a backing array}, invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, puts the array to {@code buffer}, and returns the {@code buffer}.
     */
    @DisplayName("""
            should invoke set(array[HelloWorld.BYTES]),
            and put the array to the buffer"""
    )
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.set(array)> will return given <array>
        stub_set_array_will_return_the_array();
        // create a direct buffer
        final ByteBuffer buffer = adjust(ByteBuffer.allocateDirect(HelloWorld.BYTES << 1));
        JavaNioByteBufferUtils.print(buffer);
        // assume the buffer does not have a backing array
        Assumptions.assumeFalse(
                buffer.hasArray(),
                "failed to assume that a direct buffer does not have a backing array"
        );
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.set(byte[12])> invoked, once
        final var array = verify_set_array12_invoked_once();
        // verify, <buffer.put(array)> invoked, once

        // assert, <result> is same as <buffer>
        Assertions.assertSame(buffer, result);
    }
}
