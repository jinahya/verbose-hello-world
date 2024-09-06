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
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@SuppressWarnings({
        "java:S101",  // class _Message
        "java:S6217"  // remove permits list; all permitted classes are in the same file
})
abstract sealed class CalcMessage<T extends CalcMessage<T>>
        permits CalcMessage.OfArray, CalcMessage.OfBuffer {

    // ------------------------------------------------------------------------------------ SEQUENCE
    private static final int INDEX_SEQUENCE = 0;

    private static final int LENGTH_SEQUENCE = Byte.BYTES;

    // ------------------------------------------------------------------------------------ OPERATOR
    private static final int INDEX_OPERATOR = INDEX_SEQUENCE + LENGTH_SEQUENCE;

    private static final int LENGTH_OPERATOR = CalcOperator.NAME_LENGTH;

    // ------------------------------------------------------------------------------------- OPERAND
    private static final int INDEX_OPERAND = INDEX_OPERATOR + LENGTH_OPERATOR;

    private static final int LENGTH_OPERAND = Byte.BYTES;

    // -------------------------------------------------------------------------------------- RESULT
    private static final int INDEX_RESULT = INDEX_OPERAND + LENGTH_OPERAND;

    private static final int LENGTH_RESULT = 1;

    // --------------------------------------------------------------------------------------- BYTES

    /**
     * The number of bytes enough to hold a message.
     */
    private static final int BYTES = INDEX_RESULT + LENGTH_RESULT;

    // ---------------------------------------------------------------------------------------------

    /**
     * A message class internally uses an array of {@value #BYTES} bytes.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    static final class OfArray extends CalcMessage<OfArray> {

        private static byte operand(final int operand1, final int operand2) {
            return (byte) (((operand1 & 0xF) << 4) | (operand2 & 0xF));
        }

        // -------------------------------------------------------------------------------- sequence
        @Override
        int sequence() {
            return array[INDEX_SEQUENCE] & 0xFF;
        }

        @Override
        OfArray sequence(final int sequence) {
            array[INDEX_SEQUENCE] = (byte) sequence;
            return this;
        }

        // -------------------------------------------------------------------------------- operator
        @Override
        CalcOperator operator() {
            final var name = new String(array, INDEX_OPERATOR, LENGTH_OPERATOR,
                                        StandardCharsets.US_ASCII);
            if (ThreadLocalRandom.current().nextBoolean()) {
                return CalcOperator.cachedValueOf(name);
            }
            try {
                return CalcOperator.valueOf(name);
            } catch (final IllegalArgumentException iae) {
                return null;
            }
        }

        @Override
        OfArray operator(final CalcOperator operator) {
            final var src = Optional.ofNullable(operator)
                    .map(v -> v.name().getBytes(StandardCharsets.US_ASCII))
                    .orElseGet(() -> new byte[LENGTH_OPERATOR]);
            assert src.length == CalcOperator.NAME_LENGTH;
            System.arraycopy(
                    src,
                    0,
                    array,
                    INDEX_OPERATOR,
                    LENGTH_OPERATOR
            );
            return this;
        }

        // -------------------------------------------------------------------------------- operand1
        @Override
        int operand1() {
            return (array[INDEX_OPERAND] << 24) >> 28;
        }

        @Override
        OfArray operand1(final int operand1) {
            array[INDEX_OPERAND] = operand(operand1, operand2());
            return this;
        }

        // -------------------------------------------------------------------------------- operand2
        @Override
        int operand2() {
            return (array[INDEX_OPERAND] << 28) >> 28;
        }

        @Override
        OfArray operand2(final int operand2) {
            array[INDEX_OPERAND] = operand(operand1(), operand2);
            return this;
        }

        // ---------------------------------------------------------------------------------- result
        @Override
        int result() {
            return array[INDEX_RESULT];
        }

        OfArray result(final int result) {
            array[INDEX_RESULT] = (byte) result;
            return this;
        }

        // ----------------------------------------------------------------------------------- array
        OfArray writeToServer(final OutputStream stream) throws IOException {
            stream.write(array, 0, INDEX_RESULT);
            return this;
        }

        <T extends OutputStream> OfArray writeToServerAndAccept(
                final T stream, final Consumer<? super T> consumer)
                throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            Objects.requireNonNull(consumer, "consumer is null");
            final var result = writeToServer(stream);
            consumer.accept(stream);
            return result;
        }

        <T extends Socket> OfArray writeToServerAndAccept(
                final T socket,
                final Function<? super Socket, ? extends Consumer<? super OutputStream>> function)
                throws IOException {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(function, "function is null");
            return writeToServerAndAccept(socket.getOutputStream(), s -> {
                function.apply(socket).accept(s);
            });
        }

        OfArray readFromServer(final InputStream stream) throws IOException {
            if (stream.readNBytes(array, 0, array.length) != array.length) {
                throw new EOFException("premature efo");
            }
            return this;
        }

        OfArray readFromClient(final InputStream stream) throws IOException {
            if (stream.readNBytes(array, 0, INDEX_RESULT) != INDEX_RESULT) {
                throw new EOFException("premature eof");
            }
            return this;
        }

        OfArray writeToClient(final OutputStream stream) throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            stream.write(array, 0, array.length);
            return this;
        }

        <T extends OutputStream> OfArray writeToClientAndAccept(final T stream,
                                                                final Consumer<? super T> consumer)
                throws IOException {
            Objects.requireNonNull(consumer, "consumer is null");
            writeToClient(stream);
            consumer.accept(stream);
            return this;
        }

        <T extends Socket> OfArray writeToClientAndAccept(
                final T socket, final Consumer<? super OutputStream> consumer)
                throws IOException {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(consumer, "consumer is null");
            return writeToClientAndAccept(socket.getOutputStream(), consumer);
        }

        <T extends Socket> OfArray writeToClientAndAccept(
                final T socket,
                final Function<? super T, ? extends Consumer<? super OutputStream>> function)
                throws IOException {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(function, "function is null");
            return writeToClientAndAccept(socket, s -> {
                function.apply(socket).accept(s);
            });
        }

        private OfArray sendToServer_(final DatagramSocket socket, final SocketAddress address)
                throws IOException {
            final var packet = new DatagramPacket(
                    array,        // <buf>
                    0,            // <offset>
                    INDEX_RESULT, // <length>
                    address       // <address>
            );
            socket.send(packet);
            assert packet.getLength() == INDEX_RESULT;
            return this;
        }

        OfArray sendToServer(final DatagramSocket socket) throws IOException {
            if (!Objects.requireNonNull(socket, "socket is null").isConnected()) {
                throw new IllegalArgumentException("socket is not connected");
            }
            return sendToServer_(socket, socket.getRemoteSocketAddress());
        }

        OfArray sendToServer(final DatagramSocket socket, final SocketAddress address)
                throws IOException {
            if (Objects.requireNonNull(socket, "socket is null").isConnected()) {
                throw new IllegalArgumentException("socket is connected");
            }
            Objects.requireNonNull(address, "address is null");
            return sendToServer_(socket, address);
        }

        OfArray receiveFromServer(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(
                    array,       // <buf>
                    0,           // <offset>
                    array.length // <offset>
            );
            socket.receive(packet);
            assert packet.getLength() == array.length;
            return this;
        }

        OfArray receiveFromClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(
                    array,       // <buf>
                    0,           // <offset>
                    INDEX_RESULT // <length>
            );
            socket.receive(packet);
            assert packet.getLength() == INDEX_RESULT;
            address = packet.getSocketAddress();
            return this;
        }

        OfArray sendToClient(final DatagramSocket socket) throws IOException {
            Objects.requireNonNull(socket, "socket is null");
            if (address == null) {
                throw new IllegalStateException("address is null");
            }
            final var packet = new DatagramPacket(
                    array,
                    0,
                    array.length,
                    address
            );
            socket.send(packet);
            assert packet.getLength() == array.length;
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final byte[] array = new byte[BYTES];

        private transient SocketAddress address;
    }

    /**
     * A message internally uses a byte buffer of {@value #BYTES} bytes.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    static final class OfBuffer extends CalcMessage<OfBuffer> {

        // -------------------------------------------------------------------------------- sequence
        @Override
        int sequence() {
            return ofArray.sequence();
        }

        @Override
        OfBuffer sequence(final int sequence) {
            ofArray.sequence(sequence);
            return this;
        }

        // -------------------------------------------------------------------------------- operator
        @Override
        CalcOperator operator() {
            return ofArray.operator();
        }

        OfBuffer operator(final CalcOperator operator) {
            ofArray.operator(operator);
            return this;
        }

        // -------------------------------------------------------------------------------- operand1
        @Override
        int operand1() {
            return ofArray.operand1();
        }

        @Override
        OfBuffer operand1(final int operand1) {
            ofArray.operand1(operand1);
            return this;
        }

        // -------------------------------------------------------------------------------- operand2
        @Override
        int operand2() {
            return ofArray.operand2();
        }

        @Override
        OfBuffer operand2(final int operand2) {
            ofArray.operand2(operand2);
            return this;
        }

        // ---------------------------------------------------------------------------------- result
        @Override
        int result() {
            return ofArray.result();
        }

        @Override
        OfBuffer result(final int result) {
            ofArray.result(result);
            return this;
        }

        // ---------------------------------------------------------------------------------- buffer
        boolean hasRemaining() {
            return buffer.hasRemaining();
        }

        OfBuffer readyToWriteToServer() {
            buffer.limit(INDEX_RESULT).position(0);
            return this;
        }

        OfBuffer readyToReadFromServer() {
            buffer.clear();
            return this;
        }

        OfBuffer readyToReadFromClient() {
            return readyToWriteToServer();
        }

        OfBuffer readyToWriteToClient() {
            return readyToReadFromServer();
        }

        int write(final WritableByteChannel channel) throws IOException {
            return channel.write(buffer);
        }

        int read(final ReadableByteChannel channel) throws IOException {
            return channel.read(buffer);
        }

        <A> OfBuffer read(final AsynchronousSocketChannel channel, final A attachment,
                          final CompletionHandler<Integer, ? super A> handler) {
            channel.read(
                    buffer,           // <dst>
                    1L,               // <timeout>
                    TimeUnit.SECONDS, // <unit>
                    attachment,       // <attachment>
                    handler           // <handler>
            );
            return this;
        }

        <A> OfBuffer write(final AsynchronousSocketChannel channel, final A attachment,
                           final CompletionHandler<Integer, ? super A> handler) {
            channel.write(
                    buffer,           // <src>
                    1L,               // <timeout>
                    TimeUnit.SECONDS, // <unit>
                    attachment,       // <attachment>
                    handler           // <handler>
            );
            return this;
        }

        /**
         * Sends this message, through specified channel, to specified target.
         *
         * @param channel the channel through which this message is sent.
         * @param target  the target address to which this message is sent.
         * @return this message.
         * @throws IOException if an I/O error occurs.
         */
        OfBuffer sendToServer(final DatagramChannel channel, final SocketAddress target)
                throws IOException {
            if (Objects.requireNonNull(channel, "channel is null").isConnected()) {
                throw new IllegalArgumentException("channel is connected");
            }
            Objects.requireNonNull(target, "target is null");
            readyToWriteToServer();
            final int w = channel.send(
                    buffer, // <src>
                    target  // <target>
            );
            assert w == INDEX_RESULT;
            return this;
        }

        /**
         * Sends this message through specified {@link DatagramChannel#isConnected() connected}
         * channel.
         *
         * @param channel the {@link DatagramChannel#isConnected() connected} channel through which
         *                this message is sent.
         * @return this message.
         * @throws IOException if an I/O error occurs.
         */
        OfBuffer sendToServer(final DatagramChannel channel) throws IOException {
            if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
                throw new IllegalArgumentException("channel is not connected");
            }
            readyToWriteToServer();
            final var w = channel.write(
                    buffer // <src>
            );
            assert w == INDEX_RESULT;
            assert !buffer.hasRemaining();
            return this;
        }

        OfBuffer receiveFromServer(final DatagramChannel channel) throws IOException {
            readyToReadFromServer();
            source = channel.receive(
                    buffer // <dst>
            );
            return this;
        }

        OfBuffer receiveFromClient(final DatagramChannel channel) throws IOException {
            readyToReadFromClient();
            source = channel.receive(
                    buffer // <dst>
            );
            assert source != null;
            return this;
        }

        OfBuffer sendToClient(final DatagramChannel channel) throws IOException {
            readyToWriteToClient();
            final int w = channel.send(
                    buffer, // <src>
                    source  // <target>
            );
            assert w == buffer.position();
            assert !buffer.hasRemaining();
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final OfArray ofArray = new OfArray();

        private final ByteBuffer buffer = ByteBuffer.wrap(ofArray.array);

        private transient SocketAddress source;
    }

    // ------------------------------------------------------------------------------------ sequence
    abstract int sequence();

    abstract T sequence(final int sequence);

    final T sequence(final AtomicInteger sequence) {
        return sequence(sequence.getAndIncrement());
    }

    // ------------------------------------------------------------------------------------ operator

    /**
     * Returns current value of {@code operator} property.
     *
     * @return current value of {@code operator} property.
     */
    abstract CalcOperator operator();

    /**
     * Replaces current value of {@code operator} property with specified value.
     *
     * @param operator new value for the {@code operator} property.
     * @return this message.
     */
    abstract T operator(CalcOperator operator);

    // ------------------------------------------------------------------------------------ operand1

    /**
     * Returns current value of {@code operand1} property.
     *
     * @return current value of the {@code operand1} property.
     */
    abstract int operand1();

    /**
     * Replaces current value of {@code operand1} property with the lower {@code 4} bits of
     * specified value.
     *
     * @param operand1 new value for the {@code operand1} property.
     * @return this message.
     */
    abstract T operand1(int operand1);

    // ------------------------------------------------------------------------------------ operand2

    /**
     * Returns current value of {@code operand2} property.
     *
     * @return current value of the {@code operand2} property.
     */
    abstract int operand2();

    /**
     * Replaces current value of {@code operand2} property with the lower {@code 4} bits of
     * specified value.
     *
     * @param operand2 new value for the {@code operand2} property.
     * @return this message.
     */
    abstract T operand2(int operand2);

    // -------------------------------------------------------------------------------------- result

    /**
     * Calculates and sets the {@code result} with current value of {@code operator},
     * {@code operand1}, and {@code operand2}.
     *
     * @return this message.
     * @see #calculate(Executor, Consumer)
     */
    final T calculate() {
        final var operator = operator();
        if (operator == null) {
            throw new IllegalStateException("no operator set");
        }
        final var operand1 = operand1();
        final var operand2 = operand2();
        log.debug("calculating result...");
        final var result = operator.applyAsInt(operand1, operand2);
        return result(result);
    }

    /**
     * Submits a command which {@link #calculate() calculates} the {@code result} with current value
     * of {@code operator}, {@code operand1}, and {@code operand2}, to specified executor, and
     * accepts this message to specified consumer.
     *
     * @param executor the executor to which a command is summited.
     * @param consumer the consumer to be accepted with this message.
     * @return this message.
     * @see #calculate()
     */
    @SuppressWarnings({"unchecked"})
    final T calculate(final Executor executor, final Consumer<? super T> consumer) {
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(consumer, "consumer is null");
        executor.execute(() -> consumer.accept(calculate()));
        return (T) this;
    }

    /**
     * Returns current value of {@code result} property.
     *
     * @return current value of the {@code result} property.
     */
    abstract int result();

    /**
     * Replaces current value of {@code result} property with specified value.
     *
     * @param result new value for the {@code result} property.
     * @return this instance.
     */
    abstract T result(int result);

    // ----------------------------------------------------------------------------------------- log

    /**
     * Logs out this message's current status.
     */
    @SuppressWarnings({"unchecked"})
    final T log() {
        log.info("[{}] {} {} {} {}",
                 String.format("%3d", sequence()),
                 operator(),
                 String.format("%+2d", operand1()),
                 String.format("%+2d", operand2()),
                 String.format("%+3d", result())
        );
        return (T) this;
    }

    /**
     * Randomizes this message's {@code operator}, {@code operand1}, and {@code operand2}.
     *
     * @return this message.
     */
    final T randomize() {
        return operator(CalcOperator.randomValue())
                .operand1(ThreadLocalRandom.current().nextInt())
                .operand2(ThreadLocalRandom.current().nextInt())
                ;
    }
}
