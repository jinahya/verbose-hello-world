package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class __CalcMessage2 {

    static final int BYTES = 5;

    private static final int INDEX_OPERATOR = 0;

    private static final int INDEX_OPERAND = 3;

    private static final int INDEX_RESULT = 4;

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with specified values.
     *
     * @param operator an operator.
     * @param operand1 the first operand.
     * @param operand2 the second operand.
     * @return a new instance.
     */
    public static __CalcMessage2 newInstance(final _CalcOperator operator, final int operand1,
                                             final int operand2) {
        return new __CalcMessage2()
                .operator(operator)
                .operand1(operand1)
                .operand2(operand2)
                ;
    }

    /**
     * Creates a new instance with random {@code operator}, {@code operand1}, and {@code operand2}.
     *
     * @return a new instance with random {@code operator}, {@code operand1}, and {@code operand2}.
     */
    public static __CalcMessage2 newRandomInstance() {
        return newInstance(
                _CalcOperator.randomValue(),
                ThreadLocalRandom.current().nextInt(),
                ThreadLocalRandom.current().nextInt()
        );
    }

    /**
     * Reads values from specified input stream, and set to this message.
     *
     * @param stream the input stream from which values are read.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    static __CalcMessage2 readInstance(final InputStream stream) throws IOException {
        return new __CalcMessage2().read(stream);
    }

    /**
     * Reads values from specified byte channel, and set to this message.
     *
     * @param channel the byte channel from which values are read.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    public static __CalcMessage2 readInstance(final ReadableByteChannel channel)
            throws IOException {
        return new __CalcMessage2().read(channel);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new copy of specified instance.
     *
     * @param instance the instance to be copied.
     * @return a new copy of {@code instance}.
     */
    static __CalcMessage2 copyOf(final __CalcMessage2 instance) {
        Objects.requireNonNull(instance, "instance is null");
        final var copy = new __CalcMessage2();
        copy.buffer.put(instance.buffer.clear());
        return copy;
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "buffer=" + buffer +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof __CalcMessage2 that)) return false;
        return Objects.equals(buffer.clear(), that.buffer.clear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer.clear());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Reads values from specified input stream, and set to this message.
     *
     * @param stream the input stream from which values are read.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    public __CalcMessage2 read(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var src = stream.readNBytes(BYTES);
        buffer.clear().put(src);
        return this;
    }

    /**
     * Writes values of this message to specified output stream.
     *
     * @param stream the output stream to which values are write.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    public __CalcMessage2 write(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var dst = new byte[buffer.capacity()];
        buffer.clear().get(dst);
        stream.write(dst);
        return this;
    }

    /**
     * Reads values from specified byte channel, and set to this message.
     *
     * @param channel the byte channel from which values are read.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    public __CalcMessage2 read(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        for (buffer.clear(); buffer.hasRemaining(); ) {
            final int r = channel.read(buffer);
            if (r == -1) {
                throw new EOFException("unexpected eof");
            }
        }
        return this;
    }

    /**
     * Writes values of this message to specified byte channel.
     *
     * @param channel the byte channel to which values are write.
     * @return this instance.
     * @throws IOException if an I/O error occurs.
     */
    public __CalcMessage2 write(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        for (buffer.clear(); buffer.hasRemaining(); ) {
            final var w = channel.write(buffer);
            assert w >= 0;
        }
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Calculate the result of this message.
     *
     * @return this instance.
     * @see #result()
     */
    public __CalcMessage2 calculate() {
        final var result = operator().applyAsInt(operand1(), operand2());
        buffer.limit(buffer.capacity()).position(INDEX_RESULT).put((byte) result);
        return this;
    }

    /**
     * Returns the result of this message.
     *
     * @return the result of this message.
     * @see #calculate()
     */
    public int result() {
        return buffer.limit(buffer.capacity()).position(INDEX_RESULT).get();
    }

    // ------------------------------------------------------------------------------------ operator

    /**
     * Returns the operator of this message.
     *
     * @return the operator of this message.
     */
    public _CalcOperator operator() {
        final var dst = new byte[INDEX_OPERAND];
        buffer.limit(INDEX_OPERAND).position(INDEX_OPERATOR).get(dst);
        return _CalcOperator.valueOf(new String(dst, StandardCharsets.US_ASCII));
    }

    /**
     * Replaces current operator of this message with specified value.
     *
     * @param operator new value for the operator.
     * @return this instance.
     */
    public __CalcMessage2 operator(final _CalcOperator operator) {
        Objects.requireNonNull(operator, "operator is null");
        final var src = operator.name().getBytes(StandardCharsets.US_ASCII);
        buffer.position(0).limit(src.length).put(src);
        return this;
    }

    // ------------------------------------------------------------------------------------ operand1
    public int operand1() {
        final var unsigned = (buffer.limit(INDEX_RESULT).position(INDEX_OPERAND).get() >> 4) & 0xF;
        return (unsigned << 28) >> 28;
    }

    public __CalcMessage2 operand1(final int operand1) {
        final var operand2 = operand2();
        buffer.limit(INDEX_RESULT).position(INDEX_OPERAND).put(
                (byte) (((operand1 & 0xF) << 4) | operand2)
        );
        return this;
    }

    // ------------------------------------------------------------------------------------ operand2
    public int operand2() {
        final var unsigned = buffer.limit(INDEX_RESULT).position(INDEX_OPERAND).get() & 0xF;
        return (unsigned << 28) >> 28;
    }

    public __CalcMessage2 operand2(final int operand2) {
        final var operand1 = operand1();
        buffer.limit(INDEX_RESULT).position(INDEX_OPERAND).put(
                (byte) ((operand1 << 4) | (operand2 & 0xF))
        );
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    private final ByteBuffer buffer = ByteBuffer.allocate(BYTES);
}
