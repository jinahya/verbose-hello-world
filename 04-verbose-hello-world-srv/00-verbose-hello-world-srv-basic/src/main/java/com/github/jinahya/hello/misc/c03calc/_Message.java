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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

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
    static final int BYTES = 5;

    private static final int INDEX_OPERAND = _Operator.NAME_BYTES;

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
            return _Operator.valueOf(
                    new String(array, 0, INDEX_OPERAND, StandardCharsets.US_ASCII)
            );
        }

        @Override
        OfArray setOperator(final _Operator operator) {
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
        OfArray writeToServer(final OutputStream stream, final boolean flush) throws IOException {
            stream.write(array, 0, INDEX_RESULT);
            if (flush) {
                stream.flush();
            }
            return this;
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

        OfArray writeToClient(final OutputStream stream, final boolean flush) throws IOException {
            stream.write(array, 0, array.length);
            if (flush) {
                stream.flush();
            }
            return this;
        }

        OfArray sendToServer(final DatagramSocket socket, final SocketAddress address)
                throws IOException {
            socket.send(new DatagramPacket(array, 0, INDEX_RESULT, address));
            return this;
        }

        OfArray receiveFromServer(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, 0, array.length);
            socket.receive(packet);
            return this;
        }

        OfArray receiveFromClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, 0, INDEX_RESULT);
            socket.receive(packet);
            address = packet.getSocketAddress();
            return this;
        }

        OfArray sendToClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, 0, array.length, address);
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

        Future<Integer> read(final AsynchronousByteChannel channel) {
            return channel.read(buffer);
        }

        Future<Integer> write(final AsynchronousByteChannel channel) {
            return channel.write(buffer);
        }

        <A> OfBuffer read(final AsynchronousByteChannel channel, final A attachment,
                          final CompletionHandler<Integer, ? super A> handler) {
            channel.read(buffer, attachment, handler);
            return this;
        }

        <A> OfBuffer write(final AsynchronousByteChannel channel, final A attachment,
                           final CompletionHandler<Integer, ? super A> handler) {
            channel.write(buffer, attachment, handler);
            return this;
        }

        OfBuffer sendToServer(final DatagramChannel channel, final SocketAddress target)
                throws IOException {
            readyToWriteToServer();
            final int w = channel.send(buffer, target);
            assert w == INDEX_RESULT;
            return this;
        }

        OfBuffer receiveFromServer(final DatagramChannel channel) throws IOException {
            readyToReadFromServer();
            target = channel.receive(buffer);
            return this;
        }

        OfBuffer receiveFromClient(final DatagramChannel channel) throws IOException {
            readyToReadFromClient();
            target = channel.receive(buffer);
            assert target != null;
            return this;
        }

        OfBuffer sendToClient(final DatagramChannel channel) throws IOException {
            readyToWriteToClient();
            final var w = channel.send(buffer, target);
            assert w == 1;
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final OfArray ofArray = new OfArray();

        private final ByteBuffer buffer = ByteBuffer.wrap(ofArray.array);

        private transient SocketAddress target;
    }

    // ---------------------------------------------------------------------------------------------
    private _Message() {
        super();
    }

    // ------------------------------------------------------------------------------------ operator
    abstract _Operator getOperator();

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

    @SuppressWarnings({"unchecked"})
    final T calculateResult() {
        final var operator = getOperator();
        final var operand1 = getOperand1();
        final var operand2 = getOperand2();
        log.debug("calculating result...");
        final var result = operator.applyAsInt(operand1, operand2);
        setResult(result);
        return (T) this;
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
    final void log() {
        log.info("{} {} {} {}",
                 getOperator(),
                 String.format("%+2d", getOperand1()),
                 String.format("%+2d", getOperand2()),
                 String.format("%+3d", getResult())
        );
    }

    final T randomize() {
        return setOperator(_Operator.randomValue())
                .setOperand1(ThreadLocalRandom.current().nextInt())
                .setOperand2(ThreadLocalRandom.current().nextInt())
                ;
    }
}
