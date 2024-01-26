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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
abstract sealed class __CalcMessage3<T extends __CalcMessage3<T>>
        permits __CalcMessage3.OfArray, __CalcMessage3.OfBuffer {

    private static final int BYTES = 5;

    private static final int INDEX_OPERATOR = 0;

    private static final int INDEX_OPERAND = INDEX_OPERATOR + 3;

    private static final int INDEX_RESULT = INDEX_OPERAND + 1;

    private static final int LENGTH_RESULT = BYTES - INDEX_RESULT;

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    static final class OfArray extends __CalcMessage3<OfArray> {

        private static byte operand(final int operand1, final int operand2) {
            return (byte) ((((operand1) << 4) | (operand2 & 0xF)) & 0xFF);
        }

        // -------------------------------------------------------------------------------- operator
        @Override
        _CalcOperator getOperator() {
            return _CalcOperator.valueOf(
                    new String(Arrays.copyOf(array, INDEX_OPERAND), StandardCharsets.US_ASCII)
            );
        }

        @Override
        OfArray setOperator(final _CalcOperator operator) {
            System.arraycopy(
                    operator.name().getBytes(StandardCharsets.US_ASCII),
                    0,
                    array,
                    INDEX_OPERATOR,
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
        OfArray calculateResult() {
            final var operator = getOperator();
            final var operand1 = getOperand1();
            final var operand2 = getOperand2();
            log.debug("calculating result...");
            final var result = operator.applyAsInt(operand1, operand2);
            array[INDEX_RESULT] = (byte) result;
            return this;
        }

        @Override
        int getResult() {
            return array[INDEX_RESULT];
        }

        // ----------------------------------------------------------------------------------- array
        OfArray writeToServer(final OutputStream stream, final boolean flush) throws IOException {
            stream.write(array, INDEX_OPERATOR, INDEX_RESULT);
            if (flush) {
                stream.flush();
            }
            return this;
        }

        OfArray readFromServer(final InputStream stream) throws IOException {
            if (stream.readNBytes(array, INDEX_RESULT, LENGTH_RESULT) != LENGTH_RESULT) {
                throw new EOFException("premature efo");
            }
            return this;
        }

        OfArray readFromClient(final InputStream stream) throws IOException {
            if (stream.readNBytes(array, INDEX_OPERATOR, INDEX_RESULT) != INDEX_RESULT) {
                throw new EOFException("premature eof");
            }
            return this;
        }

        OfArray writeToClient(final OutputStream stream, final boolean flush) throws IOException {
            stream.write(array, INDEX_RESULT, LENGTH_RESULT);
            if (flush) {
                stream.flush();
            }
            return this;
        }

        OfArray sendToServer(final DatagramSocket socket, final SocketAddress address)
                throws IOException {
            socket.send(new DatagramPacket(array, INDEX_OPERATOR, INDEX_RESULT, address));
            return this;
        }

        OfArray receiveFromServer(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, INDEX_RESULT, LENGTH_RESULT);
            socket.receive(packet);
            return this;
        }

        OfArray receiveFromClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, INDEX_OPERATOR, INDEX_RESULT);
            socket.receive(packet);
            address = packet.getSocketAddress();
            return this;
        }

        OfArray sendToClient(final DatagramSocket socket) throws IOException {
            final var packet = new DatagramPacket(array, INDEX_RESULT, LENGTH_RESULT, address);
            socket.send(packet);
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final byte[] array = new byte[BYTES];

        private transient SocketAddress address;
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    static final class OfBuffer extends __CalcMessage3<OfBuffer> {

        // -------------------------------------------------------------------------------- operator
        @Override
        _CalcOperator getOperator() {
            return ofArray.getOperator();
        }

        OfBuffer setOperator(final _CalcOperator operator) {
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
        OfBuffer calculateResult() {
            ofArray.calculateResult();
            return this;
        }

        @Override
        int getResult() {
            return ofArray.getResult();
        }

        // ---------------------------------------------------------------------------------- buffer
        void acceptBuffer(final Consumer<? super ByteBuffer> consumer) {
            consumer.accept(buffer);
        }

        <R> R applyBuffer(final Function<? super ByteBuffer, ? extends R> function) {
            return function.apply(buffer);
        }

        boolean hasRemaining() {
            return applyBuffer(Buffer::hasRemaining);
        }

        int remaining() {
            return applyBuffer(Buffer::remaining);
        }

        OfBuffer readToWriteToServer() {
            return applyBuffer(b -> {
                b.limit(INDEX_RESULT).position(INDEX_OPERATOR);
                assert b.remaining() == INDEX_RESULT;
                return this;
            });
        }

        OfBuffer readToReadFromServer() {
            return applyBuffer(b -> {
                b.limit(INDEX_RESULT + LENGTH_RESULT).position(INDEX_RESULT);
                assert b.remaining() == LENGTH_RESULT;
                return this;
            });
        }

        OfBuffer readyToReadFromClient() {
            return readToWriteToServer();
        }

        OfBuffer readyToWriteToClient() {
            return readToReadFromServer();
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
            return applyBuffer(b -> {
                channel.read(b, attachment, handler);
                return this;
            });
        }

        <A> OfBuffer write(final AsynchronousByteChannel channel, final A attachment,
                           final CompletionHandler<Integer, ? super A> handler) {
            return applyBuffer(b -> {
                channel.write(b, attachment, handler);
                return this;
            });
        }

        // -----------------------------------------------------------------------------------------
        private final OfArray ofArray = new OfArray();

        private final ByteBuffer buffer = ByteBuffer.wrap(ofArray.array);
    }

    // ---------------------------------------------------------------------------------------------
    __CalcMessage3() {
        super();
    }

    // ------------------------------------------------------------------------------------ operator
    abstract _CalcOperator getOperator();

    abstract T setOperator(_CalcOperator operator);

    // ------------------------------------------------------------------------------------ operand1
    abstract int getOperand1();

    abstract T setOperand1(int operand);

    // ------------------------------------------------------------------------------------ operand2
    abstract int getOperand2();

    abstract T setOperand2(int operand);

    // -------------------------------------------------------------------------------------- result
    abstract T calculateResult();

    abstract int getResult();

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
        return setOperator(_CalcOperator.randomValue())
                .setOperand1(ThreadLocalRandom.current().nextInt())
                .setOperand2(ThreadLocalRandom.current().nextInt());
    }
}
