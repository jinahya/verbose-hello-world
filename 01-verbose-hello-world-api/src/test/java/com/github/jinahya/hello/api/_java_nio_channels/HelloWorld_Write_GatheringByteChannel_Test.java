package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(GatheringByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.write(GatheringByteChannel)")
@Slf4j
class HelloWorld_Write_GatheringByteChannel_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#write(GatheringByteChannel) write(channel)} method throws
     * a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final GatheringByteChannel channel = null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(channel));
    }

    /**
     * Verifies that the {@link HelloWorld#write(GatheringByteChannel) write(channel)} method
     * invokes {@link HelloWorld#write(WritableByteChannel) write((WritableByteChannel) channel)}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("delegates to write((WritableByteChannel) channel)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.<WritableByteChannel>notNull());
        final var channel = mock(GatheringByteChannel.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write((WritableByteChannel) channel);
        assertSame(channel, result);
    }

    /**
     * Writes the hello-world-bytes as split buffers through a {@link Pipe} via it's
     * {@link Pipe#sink() sink}({@link GatheringByteChannel}), and reads them back via it's
     * {@link Pipe#source() source}({@link ScatteringByteChannel}).
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("添足 / 畵蛇")
    @Test
    void _添足_畵蛇() throws IOException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var bytes = HelloWorld__TestUtils.hello_world_byte_array();
        final var pipe = Pipe.open();
        // ----------------------------------------------------------------------------------- write
        final var writer = Thread.ofPlatform().daemon().start(() -> {
            try (var sink = pipe.sink()) {
                final var srcs = new ByteBuffer[] {
                        ByteBuffer.wrap(bytes, 0, 5), // "hello"
                        ByteBuffer.wrap(bytes, 5, 2), // ", "
                        ByteBuffer.wrap(bytes, 7, 5)  // "world"
                };
                for (var remaining = (long) HelloWorld.BYTES; remaining > 0; ) {
                    remaining -= sink.write(srcs);
                }
            } catch (final IOException ioe) {
                log.error("failed to write", ioe);
            }
        });
        // ------------------------------------------------------------------------------------ read
        try (var source = pipe.source()) {
            final var dsts = new ByteBuffer[] {
                    ByteBuffer.allocate(3), // "hel"
                    ByteBuffer.allocate(5), // "lo, w"
                    ByteBuffer.allocate(4)  // "orld"
            };
            for (var remaining = (long) HelloWorld.BYTES; remaining > 0; ) {
                final var r = source.read(dsts);
                assert r != -1;
                remaining -= r;
            }
            final var result = new byte[HelloWorld.BYTES];
            var offset = 0;
            for (final var dst : dsts) {
                dst.flip();
                final var length = dst.remaining();
                dst.get(result, offset, length);
                offset += length;
            }
            assertArrayEquals(bytes, result);
        }
        writer.join();
    }

    /**
     * A binary operator on two {@code float} operands. Each constant's {@link #name()} is exactly 3
     * ASCII characters, used as the wire format for serialization over channels. All operations
     * follow IEEE 754 semantics; no {@link ArithmeticException} is ever thrown.
     */
    private enum Operator {

        /**
         * Addition ({@code operand1 + operand2}).
         */
        ADD {
            @Override
            float calc(final float operand1, final float operand2) {
                return operand1 + operand2;
            }
        },

        /**
         * Subtraction ({@code operand1 - operand2}).
         */
        SUB {
            @Override
            float calc(final float operand1, final float operand2) {
                return operand1 - operand2;
            }
        },

        /**
         * Multiplication ({@code operand1 * operand2}).
         */
        MUL {
            @Override
            float calc(final float operand1, final float operand2) {
                return operand1 * operand2;
            }
        },

        /**
         * Division ({@code operand1 / operand2}). Division by zero yields {@code Infinity} or
         * {@code NaN}.
         */
        DIV {
            @Override
            float calc(final float operand1, final float operand2) {
                return operand1 / operand2;
            }
        },

        /**
         * Truncated modulo ({@code operand1 % operand2}). The sign of the result follows the
         * dividend.
         */
        MOD {
            @Override
            float calc(final float operand1, final float operand2) {
                return operand1 % operand2;
            }
        },

        /**
         * IEEE 754 remainder via {@link Math#IEEEremainder(double, double)}. May differ in sign
         * from {@link #MOD}.
         */
        REM {
            @Override
            float calc(final float operand1, final float operand2) {
                return (float) Math.IEEEremainder(operand1, operand2);
            }
        },

        /**
         * Minimum via {@link Math#min(float, float)}.
         */
        MIN {
            @Override
            float calc(final float operand1, final float operand2) {
                return Math.min(operand1, operand2);
            }
        },

        /**
         * Maximum via {@link Math#max(float, float)}.
         */
        MAX {
            @Override
            float calc(final float operand1, final float operand2) {
                return Math.max(operand1, operand2);
            }
        },

        /**
         * Exponentiation via {@link Math#pow(double, double)}.
         */
        POW {
            @Override
            float calc(final float operand1, final float operand2) {
                return (float) Math.pow(operand1, operand2);
            }
        },

        /**
         * Copy sign via {@link Math#copySign(float, float)}. Magnitude of {@code operand1}, sign of
         * {@code operand2}.
         */
        CPY {
            @Override
            float calc(final float operand1, final float operand2) {
                return Math.copySign(operand1, operand2);
            }
        };

        /**
         * Calculates the result of applying this operator to the given operands.
         *
         * @param operand1 the first operand.
         * @param operand2 the second operand.
         * @return the result of the operation.
         */
        abstract float calc(float operand1, float operand2);
    }

    /**
     * An example of a calculator instruction binary, which is composed of a {@link Operator}, the
     * first operand of {@code float}, the second operand of {@code float}, and a result of
     * {@code float}.
     *
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    @DisplayName("calc")
    @Test
    void __Calc() throws IOException, InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var operator = Operator.values()[
                ThreadLocalRandom.current().nextInt(Operator.values().length)
                ];
        final var operand1 = ThreadLocalRandom.current().nextFloat();
        final var operand2 = ThreadLocalRandom.current().nextFloat();
        final var result = operator.calc(operand1, operand2);
        final var pipe = Pipe.open();
        // ----------------------------------------------------------------------------------- write
        final var thread = Thread.ofPlatform().daemon().start(() -> {
            try (var sink = pipe.sink()) {
                final var srcs = new ByteBuffer[] {
                        ByteBuffer.wrap(operator.name().getBytes(StandardCharsets.US_ASCII)),
                        ByteBuffer.allocate(Float.BYTES).putFloat(operand1).flip(),
                        ByteBuffer.allocate(Float.BYTES).putFloat(operand2).flip(),
                        ByteBuffer.allocate(Float.BYTES).putFloat(result).flip()
                };
                final var total = (long) (3 + Float.BYTES * 3);
                for (var remaining = total; remaining > 0; ) {
                    final var r = sink.write(srcs);
                    assert r != -1;
                    remaining -= r;
                }
            } catch (final IOException ioe) {
                log.error("failed to write", ioe);
            }
        });
        // ------------------------------------------------------------------------------------ read
        try (var source = pipe.source()) {
            final var dsts = new ByteBuffer[] {
                    ByteBuffer.allocate(3),             // operator name (3 ASCII bytes)
                    ByteBuffer.allocate(Float.BYTES),   // first operand
                    ByteBuffer.allocate(Float.BYTES),   // second operand
                    ByteBuffer.allocate(Float.BYTES)    // result
            };
            final var total = (long) (3 + Float.BYTES * 3);
            for (var remaining = total; remaining > 0; ) {
                final var r = source.read(dsts);
                assert r != -1;
                remaining -= r;
            }
            for (final var dst : dsts) {
                dst.flip();
            }
            final var readOperator = Operator.valueOf(
                    new String(dsts[0].array(), StandardCharsets.US_ASCII)
            );
            final var readOperand1 = dsts[1].getFloat();
            final var readOperand2 = dsts[2].getFloat();
            final var readResult = dsts[3].getFloat();
            assertEquals(operator, readOperator);
            assertEquals(operand1, readOperand1);
            assertEquals(operand2, readOperand2);
            assertEquals(result, readResult);
            assertEquals(readOperator.calc(readOperand1, readOperand2), readResult);
            log.debug("({} {} {}) = {}", readOperator, readOperand1, readOperand2, readResult);
        }
        thread.join();
    }
}
