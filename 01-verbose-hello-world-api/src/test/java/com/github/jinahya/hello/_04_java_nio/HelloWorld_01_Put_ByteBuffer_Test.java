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
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorld#put(ByteBuffer) put(buffer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(buffer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_01_Put_ByteBuffer_Test extends HelloWorldTest {

    private static final Random RANDOM;

    static {
        Random random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (final NoSuchAlgorithmException nsme) {
            random = ThreadLocalRandom.current();
        }
        RANDOM = random;
    }

    private static int index(final int max, final int min) {
        assert max >= min : String.format("max(%1$d) should be GE to min(%2$d)", max, min);
        assert min >= 0 : String.format("min(%1$d) should be non-negative", min);
        return RANDOM.nextInt(0, ((max - min) >> 1) + 1);
    }

    private static int length(final int max, final int min, final int index) {
        assert max >= min : String.format("max(%1$d) should be GE to min(%2$d)", max, min);
        assert min >= 0 : String.format("min(%1$d) should be non-negative", min);
        assert index >= 0;
        final var length = RANDOM.nextInt(min, max - index + 1);
        assert length >= min;
        return length;
    }

    private static <R> R slice(final int max, final int min,
                               final IntFunction<? extends IntFunction<? extends R>> function) {
        assert max >= min : String.format("max(%1$d) should be GE to min(%2$d)", max, min);
        assert min >= 0 : String.format("min(%1$d) should be non-negative", min);
        assert function != null;
        final var index = index(max, min);
        final var length = length(max, min, index);
        return function.apply(index).apply(length);
    }

    private static ByteBuffer slice(final ByteBuffer buffer, final int min) {
        if (min > Objects.requireNonNull(buffer, "buffer is null").limit()) {
            throw new IllegalArgumentException(
                    "min(" + min + ") > buffer.limit(" + buffer.limit() + ")"
            );
        }
        return slice(
                buffer.limit(),
                min,
                i -> l -> {
                    log.debug("slicing; index: {}, length: {}", i, l);
                    final var sliced = buffer.slice(i, l);
                    assert sliced.capacity() >= min;
                    // > The new buffer's position will be zero
                    assert sliced.position() == 0;
                    // > , its capacity and its limit will be <length>,
                    assert sliced.capacity() == l;
                    assert sliced.limit() == sliced.capacity();
                    assert sliced.order() == ByteOrder.BIG_ENDIAN;
                    // > The new buffer will be direct if, and only if, this buffer is direct
                    assert sliced.isDirect() == buffer.isDirect();
                    // > , and it will be read-only if, and only if, this buffer is read-only.
                    assert sliced.isReadOnly() == buffer.isReadOnly();
                    return sliced;
                }
        );
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class ByteBufferTest {

        private static IntStream getArrayLengthStream() {
            return IntStream.of(
                    0,
                    ThreadLocalRandom.current().nextInt(16, 32)
            );
        }

        private static Stream<Arguments> getArrayArgumentsStream() {
            return getArrayLengthStream()
                    .mapToObj(byte[]::new)
                    .map(a -> Arguments.of(Named.of(String.format("array[%1$d]", a.length), a)));
        }

        private static Stream<Arguments> getArrayOffsetAndLengthArgumentsStream() {
            return getArrayLengthStream()
                    .mapToObj(byte[]::new)
                    .map(a -> slice(a.length, 0, i -> l -> Arguments.of(
                            Named.of(String.format("array[%1$d]", a.length), a),
                            Named.of("offset(" + i + ")", i),
                            Named.of("length(" + l + ")", l)
                    )));
        }

        private static IntStream getCapacityStream() {
            return getArrayLengthStream();
        }

        @DisplayName("wrap(array)")
        @MethodSource({"getArrayArgumentsStream"})
        @ParameterizedTest
        void _wrap_array(final byte[] array) {
            // ------------------------------------------------------------------------------- given
            // empty
            // -------------------------------------------------------------------------------- when
            final var buffer = ByteBuffer.wrap(array);
            JavaNioByteBufferUtils.print(buffer);
            // -------------------------------------------------------------------------------- then
            assert buffer.hasArray();
            assert buffer.capacity() == array.length;
            assert buffer.limit() == buffer.capacity();
            assert buffer.position() == 0;
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            assert buffer.array() == array;
            assert buffer.arrayOffset() == 0;
            // -------------------------------------------------------------------------------- when
            final var sliced = slice(buffer, 0);
            JavaNioByteBufferUtils.print(sliced);
            // -------------------------------------------------------------------------------- then
            assert sliced != buffer;
            assert sliced.hasArray();
            assert sliced.array() == buffer.array();
        }

        @DisplayName("wrap(array, offset, length)")
        @MethodSource({"getArrayOffsetAndLengthArgumentsStream"})
        @ParameterizedTest
        void _wrap_arrayOffsetAndLength(final ArgumentsAccessor accessor) {
            // ------------------------------------------------------------------------------- given
            final var array = accessor.get(0, byte[].class);
            final var offset = accessor.getInteger(1);
            final var length = accessor.getInteger(2);
            // -------------------------------------------------------------------------------- when
            final var buffer = ByteBuffer.wrap(array, offset, length);
            JavaNioByteBufferUtils.print(buffer);
            // -------------------------------------------------------------------------------- then
            assert buffer.hasArray();
            assert buffer.capacity() == array.length;
            assert buffer.position() == offset;
            assert buffer.limit() == offset + length;
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            assert buffer.array() == array;
            assert buffer.arrayOffset() == 0;
            // -------------------------------------------------------------------------------- when
            final var sliced = slice(buffer, 0);
            JavaNioByteBufferUtils.print(sliced);
            // -------------------------------------------------------------------------------- then
            assert sliced != buffer;
            assert sliced.hasArray();
            assert sliced.array() == buffer.array();
        }

        @DisplayName("allocate(capacity)")
        @MethodSource({"getCapacityStream"})
        @ParameterizedTest
        void __allocate(final int capacity) {
            // ------------------------------------------------------------------------------- given
            // empty
            // -------------------------------------------------------------------------------- when
            final var buffer = ByteBuffer.allocate(capacity);
            JavaNioByteBufferUtils.print(buffer);
            // -------------------------------------------------------------------------------- then
            assert buffer.position() == 0;
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            assert buffer.hasArray();
            assert buffer.arrayOffset() == 0;
            // -------------------------------------------------------------------------------- when
            final var sliced = slice(buffer, 0);
            JavaNioByteBufferUtils.print(sliced);
            // -------------------------------------------------------------------------------- then
            assert sliced != buffer;
            assert sliced.hasArray();
        }

        @DisplayName("allocateDirect(capacity)")
        @MethodSource({"getCapacityStream"})
        @ParameterizedTest
        void __allocateDirect(final int capacity) {
            // -------------------------------------------------------------------------------- when
            final var buffer = ByteBuffer.allocateDirect(capacity);
            JavaNioByteBufferUtils.print(buffer);
            // -------------------------------------------------------------------------------- then
            assert buffer.isDirect();
            assert buffer.position() == 0;
            assert buffer.limit() == buffer.capacity();
            assert buffer.order() == ByteOrder.BIG_ENDIAN;
            // > Whether or not it has a backing array is unspecified
            final var hasArray = buffer.hasArray();
            // -------------------------------------------------------------------------------- when
            final var sliced = slice(buffer, 0);
            JavaNioByteBufferUtils.print(sliced);
            // -------------------------------------------------------------------------------- then
            assert sliced != buffer;
            assert sliced.isDirect();
            assert sliced.hasArray() == hasArray;
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method throws a
     * {@link NullPointerException} when the {@code buffer} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <buffer> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_BufferIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var buffer = (ByteBuffer) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.put(buffer)> throws a <NullPointerException>
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
            should throw a <BufferOverflowException>
            when <buffer.remaining()> is less than <12>"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowBufferOverflowException_BufferRemainingIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // ------------------------------------------------------------------------------- when/then
        return Stream.of(
                ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)),
                ByteBuffer.allocateDirect(ThreadLocalRandom.current().nextInt(HelloWorld.BYTES))
        ).map(b -> DynamicTest.dynamicTest(
                "should throw a <BufferOverflowException> for " + b + " (" + b.remaining() + ")",
                () -> {
                    // assert, <service.put(b)> throws a <BufferOverflowException>
                    Assertions.assertThrows(
                            BufferOverflowException.class,
                            () -> service.put(b)
                    );
                }
        ));
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a
     * byte buffer which {@link ByteBuffer#hasArray() has a backing array}, invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with {@code buffer.array()} and
     * ({@code buffer.arrayOffset() + buffer.position()}), and returns the {@code buffer} as its
     * {@link ByteBuffer#position() position} increased by
     * {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should invoke <set(buffer.array(), buffer.arrayOffset() + buffer.position())>
            when the <buffer> has a backing array"""
    )
    @Test
    void __BufferHasBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <set(array, index)> to just return the <array>
        Mockito.doAnswer(i -> {
                    final var array = i.getArgument(0, byte[].class);
                    final var index = i.getArgument(0, Integer.class); // NOSONAR
                    return array;
                })
                .when(service)
                .set(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        // prepare a byte buffer which has a backing-array, and has enough remaining.
        final var buffer = Mockito.spy(
                slice(ByteBuffer.allocate(HelloWorld.BYTES << 1), HelloWorld.BYTES)
        );
        JavaNioByteBufferUtils.print(buffer);
        assert buffer.hasArray();
        assert buffer.remaining() >= HelloWorld.BYTES;
        final var position = buffer.position(); // NOSONAR
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.set(buffer.array(), buffer.arrayOffset() + position)> invoked, once

        // verify, <buffer.position(position + 12)> invoked, once
        JavaNioByteBufferUtils.print(buffer);

        // verify, no more interactions with <buffer>

        // assert, <result> is same as <buffer>
        Assertions.assertSame(buffer, result);
    }

    /**
     * Verifies that the {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when invoked with a
     * byte buffer which does not {@link ByteBuffer#hasArray() have a backing array}, invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, puts the {@code array} to {@code buffer}, and returns the {@code buffer}.
     */
    @DisplayName("""
            should invoke <set(array[12])>,
            and put the <array> to the <buffer>"""
    )
    @Test
    void __BufferDoesNotHaveBackingArray() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.set(array)> to just return the <array>
        stub_set_array_will_return_the_array();
        // create a direct buffer
        final var buffer = Mockito.spy(
                slice(ByteBuffer.allocateDirect(HelloWorld.BYTES << 1), HelloWorld.BYTES)
        );
        JavaNioByteBufferUtils.print(buffer);
        assert buffer.isDirect();
        assert buffer.remaining() >= HelloWorld.BYTES;
        // assume, the <buffer> does not have a backing array
        Assumptions.assumeFalse(
                buffer.hasArray(),
                "failed to assume that a direct buffer does not have a backing array"
        );
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(buffer);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.set(array[12])> invoked, once
        final var array = verify_set_array12_invoked_once();
        // verify, <buffer.put(array)> invoked, once

        // verify, no more interactions with <buffer.

        // assert, <result> is same as <buffer>
        Assertions.assertSame(buffer, result);
    }
}
