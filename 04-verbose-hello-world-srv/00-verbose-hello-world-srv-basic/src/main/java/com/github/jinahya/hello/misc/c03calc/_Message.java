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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@SuppressWarnings({
        "java:S101",  // class _Message
        "java:S6217"  // remove permits list; all permitted classes are in the same file
})
abstract sealed class _Message<T extends _Message<T>>
        permits _Message.OfArray, _Message.OfBuffer {

    // ---------------------------------------------------------------------------------------------

    /**
     * The number of bytes enough to hold a message.
     */
    private static final int BYTES = 5;

    private static final int INDEX_OPERAND = _Operator.NAME_LENGTH;

    private static final int LENGTH_RESULT = 1;

    private static final int INDEX_RESULT = INDEX_OPERAND + LENGTH_RESULT;

    /**
     * A message class internally uses an array of {@value #BYTES} bytes.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    static final class OfArray extends _Message<OfArray> {

        private static byte operand(final int operand1, final int operand2) {
            return (byte) ((((operand1) << 4) | (operand2 & 0xF)) & 0xFF);
        }

        // -------------------------------------------------------------------------------- operator
        @Override
        _Operator getOperator() {
            final var name = new String(array, 0, INDEX_OPERAND, StandardCharsets.US_ASCII);
            if (ThreadLocalRandom.current().nextBoolean()) {
                return _Operator.cachedValueOf(name);
            }
            return _Operator.valueOf(name);
        }

        @Override
        OfArray setOperator(final _Operator operator) {
            Objects.requireNonNull(operator, "operator is null");
            System.arraycopy(
                    operator.name().getBytes(StandardCharsets.US_ASCII),
                    0,
                    array,
                    0,
                    INDEX_OPERAND
            );
            return this;
        }

        // -------------------------------------------------------------------------------- operand1
        @Override
        int getOperand1() {
            return (array[INDEX_OPERAND] << 24) >> 28;
        }

        @Override
        OfArray setOperand1(final int operand1) {
            array[INDEX_OPERAND] = operand(operand1, getOperand2());
            return this;
        }

        // -------------------------------------------------------------------------------- operand2
        @Override
        int getOperand2() {
            return (array[INDEX_OPERAND] << 28) >> 28;
        }

        @Override
        OfArray setOperand2(final int operand2) {
            array[INDEX_OPERAND] = operand(getOperand1(), operand2);
            return this;
        }

        // ---------------------------------------------------------------------------------- result
        @Override
        int getResult() {
            return array[INDEX_RESULT];
        }

        OfArray setResult(final int result) {
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

        OfArray sendToServer(final DatagramSocket socket, final SocketAddress address)
                throws IOException {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(address, "address is null");
            socket.send(new DatagramPacket(
                    array,        // <buf>
                    0,            // <offset>
                    INDEX_RESULT, // <length>
                    address       // <address>
            ));
            return this;
        }

        OfArray sendToServer(final DatagramSocket socket) throws IOException {
            if (!Objects.requireNonNull(socket, "socket is null").isConnected()) {
                throw new IllegalArgumentException("socket is not connected: " + socket);
            }
            return sendToServer(
                    socket,
                    socket.getRemoteSocketAddress()
            );
        }

        OfArray receiveFromServer(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(
                    array,       // <buf>
                    0,           // <offset>
                    array.length // <offset>
            );
            socket.receive(packet);
            return this;
        }

        OfArray receiveFromClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(
                    array,       // <buf>
                    0,           // <offset>
                    INDEX_RESULT // <length>
            );
            socket.receive(packet);
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
    static final class OfBuffer extends _Message<OfBuffer> {

        // -------------------------------------------------------------------------------- operator
        @Override
        _Operator getOperator() {
            return ofArray.getOperator();
        }

        OfBuffer setOperator(final _Operator operator) {
            ofArray.setOperator(operator);
            return this;
        }

        // -------------------------------------------------------------------------------- operand1
        @Override
        int getOperand1() {
            return ofArray.getOperand1();
        }

        @Override
        OfBuffer setOperand1(final int operand1) {
            ofArray.setOperand1(operand1);
            return this;
        }

        // -------------------------------------------------------------------------------- operand2
        @Override
        int getOperand2() {
            return ofArray.getOperand2();
        }

        @Override
        OfBuffer setOperand2(final int operand2) {
            ofArray.setOperand2(operand2);
            return this;
        }

        // ---------------------------------------------------------------------------------- result
        @Override
        int getResult() {
            return ofArray.getResult();
        }

        @Override
        OfBuffer setResult(final int result) {
            ofArray.setResult(result);
            return this;
        }

        // ---------------------------------------------------------------------------------- buffer
        OfBuffer print(PrintStream printer) {
            JavaNioByteBufferUtils.print(buffer, printer);
            return this;
        }

        boolean hasRemaining() {
            return buffer.hasRemaining();
        }

        int remaining() {
            return buffer.remaining();
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
            Objects.requireNonNull(channel, "channel is null");
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
            final var w = channel.send(
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

    // ------------------------------------------------------------------------------------ operator

    /**
     * Returns current value of {@code operator} property.
     *
     * @return current value of {@code operator} property.
     */
    abstract _Operator getOperator();

    /**
     * Replaces current value of {@code operator} property with specified value.
     *
     * @param operator new value for the {@code operator} property.
     * @return this message.
     */
    abstract T setOperator(_Operator operator);

    // ------------------------------------------------------------------------------------ operand1
    abstract int getOperand1();

    abstract T setOperand1(int operand);

    // ------------------------------------------------------------------------------------ operand2
    abstract int getOperand2();

    abstract T setOperand2(int operand);

    // -------------------------------------------------------------------------------------- result
    @SuppressWarnings({"unchecked"})
    final T calculateResult(final Executor executor, final Consumer<? super T> consumer) {
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try {
            executor.execute(() -> consumer.accept(calculateResult()));
        } catch (final RejectedExecutionException ree) {
            log.error("failed to submit task", ree);
        }
        return (T) this;
    }

    final T calculateResult() {
        final var operator = getOperator();
        final var operand1 = getOperand1();
        final var operand2 = getOperand2();
        log.debug("calculating result...");
        final var result = operator.applyAsInt(operand1, operand2);
        return setResult(result);
    }

    /**
     * Returns current value of {@code result} property.
     *
     * @return current value of the {@code result} property.
     */
    abstract int getResult();

    /**
     * Replaces current value of {@code result} property with specified value.
     *
     * @param result new value for the {@code result} property.
     * @return this instance.
     */
    abstract T setResult(int result);

    // ----------------------------------------------------------------------------------------- log

    /**
     * Logs out this message's current status.
     *
     * @param index an index to print.
     */
    @SuppressWarnings({"unchecked"})
    final T log(final int index) {
        log.info("[{}] {} {} {} {}",
                 String.format("%6d", index),
                 getOperator(),
                 String.format("%+2d", getOperand1()),
                 String.format("%+2d", getOperand2()),
                 String.format("%+3d", getResult())
        );
        return (T) this;
    }

    /**
     * Logs out this message's current status.
     */
    final T log() {
        return log(0);
    }

    /**
     * Randomizes this message.
     *
     * @return this message.
     */
    final T randomize() {
        return setOperator(_Operator.randomValue())
                .setOperand1(ThreadLocalRandom.current().nextInt())
                .setOperand2(ThreadLocalRandom.current().nextInt())
                .setResult(0)
                ;
    }
}
