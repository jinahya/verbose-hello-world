package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;

@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class _CalcMessage {

    // ------------------------------------------------------------------------------------ operator
    private static final int INDEX_OPERATOR = 0;

    private static final int LIMIT_OPERATOR = INDEX_OPERATOR + _CalcOperator.NAME_BYTES;

    // ------------------------------------------------------------------------------------ operands
    private static final int INDEX_OPERAND1 = LIMIT_OPERATOR;

    private static final int LIMIT_OPERAND1 = INDEX_OPERAND1 + Byte.BYTES;

    private static final int INDEX_OPERAND2 = LIMIT_OPERAND1;

    private static final int LIMIT_OPERAND2 = INDEX_OPERAND2 + Byte.BYTES;

    // -------------------------------------------------------------------------------------- result
    private static final int INDEX_RESULT = LIMIT_OPERAND2;

    private static final int LENGTH_RESULT = Byte.BYTES;

    private static final int LIMIT_RESULT = INDEX_RESULT + LENGTH_RESULT;

    // ---------------------------------------------------------------------------------------------
    static final int LENGTH_REQUEST = LIMIT_OPERAND2;

    static final int LENGTH_RESPONSE = LIMIT_RESULT;

    private static final int LENGTH = LENGTH_REQUEST + LENGTH_RESPONSE;

    // ---------------------------------------------------------------------------- operator/operand
    private static final List<_CalcOperator> OPERATORS = List.of(_CalcOperator.values());

    static {
        assert !OPERATORS.isEmpty();
    }

    private static _CalcOperator randomOperator() {
        return OPERATORS.get(
                ThreadLocalRandom.current().nextInt(OPERATORS.size())
        );
    }

    // ------------------------------------------------------------------------------------- operand
    private static byte randomOperand() {
        return (byte) ThreadLocalRandom.current().nextInt(-9, 10); // [-9..+9]
    }

//    // ---------------------------------------------------------------------------------- applyAsync
//    static CompletableFuture<ByteBuffer> applyAsync(
//            final ByteBuffer buffer,
//            final Executor executor) {
//        Objects.requireNonNull(buffer, "buffer is null");
//        Objects.requireNonNull(executor, "executor is null");
//        return CompletableFuture.supplyAsync(() -> apply(buffer), executor);
//    }
//
//    static CompletableFuture<byte[]> applyAsync(final byte[] array, final Executor executor) {
//        Objects.requireNonNull(array, "array is null");
//        return applyAsync(ByteBuffer.wrap(array), executor).handle((b, t) -> b.array());
//    }

    // ---------------------------------------------------------------------------------------------
    static _CalcMessage newInstanceForServers() {
        return new _CalcMessage()
                .readyToReceiveRequest();
    }

    static _CalcMessage newInstanceForClients() {
        return new _CalcMessage()
                .operator(randomOperator())
                .operand1(randomOperand())
                .operand2(randomOperand())
                .readyToSendRequest();
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcMessage() {
        super();
        this.buffer = ByteBuffer.allocate(LENGTH);
    }

    // ------------------------------------------------------------------------------------ blocking
    _CalcMessage sendRequest(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(buffer.array(), 0, LENGTH_REQUEST);
        stream.flush();
        return this;
    }

    _CalcMessage receiveResult(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (stream.readNBytes(buffer.array(), LENGTH_REQUEST, LENGTH_RESPONSE) < LENGTH_RESPONSE) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    _CalcMessage receiveRequest(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (stream.readNBytes(buffer.array(), 0, LENGTH_REQUEST) < LENGTH_REQUEST) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    _CalcMessage sendResult(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(buffer.array(), LENGTH_REQUEST, LENGTH_RESPONSE);
        stream.flush();
        return this;
    }

    // -------------------------------------------------------------------------------- non-blocking
    _CalcMessage send(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        channel.write(buffer);
        return this;
    }

    _CalcMessage receive(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        final var r = channel.read(buffer);
        if (r == -1) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    // -------------------------------------------------------------------------------- asynchronous
    <A> void write(final AsynchronousSocketChannel channel, final A attachment,
                   final CompletionHandler<Integer, ? super A> handler) {
        channel.write(
                buffer,                            // <src>
                _CalcConstants.WRITE_TIMEOUT,      // <timeout>
                _CalcConstants.WRITE_TIMEOUT_UNIT, // <unit>
                attachment,                        // <attachment>
                handler                            // <handler>
        );
    }

    <A> void read(final AsynchronousSocketChannel channel, final A attachment,
                  final CompletionHandler<Integer, ? super A> handler) {
        channel.read(
                buffer,                           // <dst>
                _CalcConstants.READ_TIMEOUT,      // <timeout>
                _CalcConstants.READ_TIMEOUT_UNIT, // <unit>
                attachment,                       // <attachment>
                handler                           // <handler>
        );
    }

    // ---------------------------------------------------------------------------------- udp/socket
    _CalcMessage sendRequest(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        socket.send(new DatagramPacket(buffer.array(), LENGTH_REQUEST, _CalcConstants.ADDR));
        return this;
    }

    _CalcMessage receiveResult(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        socket.receive(new DatagramPacket(buffer.array(), LENGTH_REQUEST, LENGTH_RESPONSE));
        return this;
    }

    _CalcMessage receiveRequest(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        final var packet = new DatagramPacket(buffer.array(), 0, LENGTH_REQUEST);
        socket.receive(packet);
        address = packet.getSocketAddress();
        return this;
    }

    _CalcMessage sendResult(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        assert address != null;
        socket.send(new DatagramPacket(buffer.array(), LENGTH_REQUEST, LENGTH_RESPONSE, address));
        return this;
    }

    // --------------------------------------------------------------------------------- udp/channel
    _CalcMessage sendRequest(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        readyToSendRequest();
        final var w = channel.send(buffer, _CalcConstants.ADDR);
        assert w == LENGTH_REQUEST;
        return this;
    }

    _CalcMessage receiveResult(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        readyToReceiveResult();
        address = channel.receive(buffer);
        return this;
    }

    _CalcMessage receiveRequest(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        readyToReceiveRequest();
        address = channel.receive(buffer);
        return this;
    }

    _CalcMessage sendResult(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        assert address != null;
        readyToSendResult();
        final var w = channel.send(buffer, address);
        assert w == LENGTH_RESULT;
        return this;
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Applies {@link #operator() operator} with {@link #operand1() operand1} and
     * {@link #operand2() operand2}, and set the {@link #result(byte) result}.
     *
     * @return this message.
     * @see #readyToSendResult()
     */
    _CalcMessage apply() {
        return result((byte) operator().applyAsInt(operand1(), operand2()));
    }

    // ------------------------------------------------------------------------------------ operator
    _CalcOperator operator() {
        final var nameBytes = new byte[_CalcOperator.NAME_BYTES];
        buffer.get(INDEX_OPERATOR, nameBytes);
        return _CalcOperator.valueOf(nameBytes);
    }

    _CalcMessage operator(final _CalcOperator operator) {
        Objects.requireNonNull(operator, "operator is null");
        buffer.limit(INDEX_OPERAND1).put(INDEX_OPERATOR, operator.toBytes());
        return this;
    }

    // ------------------------------------------------------------------------------------ operands
    int operands(final IntBinaryOperator operator) {
        Objects.requireNonNull(operator, "operator is null");
        return operator.applyAsInt(operand1(), operand2());
    }

    <R> R operands(final IntFunction<? extends IntFunction<? extends R>> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(operand1()).apply(operand2());
    }

    _CalcMessage operands(final byte operand1, final byte operand2) {
        return operand1(operand1).operand2(operand2);
    }

    byte operand1() {
        return buffer.get(INDEX_OPERAND1);
    }

    _CalcMessage operand1(final byte operand1) {
        buffer.limit(INDEX_OPERAND2).put(INDEX_OPERAND1, operand1);
        return this;
    }

    byte operand2() {
        return buffer.limit(INDEX_RESULT).get(INDEX_OPERAND2);
    }

    _CalcMessage operand2(final byte operand2) {
        buffer.limit(INDEX_RESULT).put(INDEX_OPERAND2, operand2);
        return this;
    }

    // -------------------------------------------------------------------------------------- result
    byte result() {
        return buffer.limit(LIMIT_RESULT).get(INDEX_RESULT);
    }

    _CalcMessage result(final byte result) {
        buffer.limit(LIMIT_RESULT).put(INDEX_RESULT, result);
        return this;
    }

    // ------------------------------------------------------------------------------------- logging
    _CalcMessage log() {
        log.debug("{}({}, {}) = {}",
                  operator(),
                  String.format("%1$+d", operand1()),
                  String.format("%1$+d", operand2()),
                  String.format("%1$+3d", result())
        );
        return this;
    }

    // -------------------------------------------------------------------------------------- buffer
    boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    private _CalcMessage readyToSendRequest() {
        buffer.position(0).limit(LENGTH_REQUEST);
        return this;
    }

    /**
     * Gets this message ready to be received from server.
     *
     * @return this message.
     */
    _CalcMessage readyToReceiveResult() {
        buffer.position(LENGTH_REQUEST).limit(LENGTH);
        return this;
    }

    private _CalcMessage readyToReceiveRequest() {
        return readyToSendRequest();
    }

    /**
     * Gets this message ready to be sent to client.
     *
     * @return this message.
     */
    _CalcMessage readyToSendResult() {
        return readyToReceiveResult();
    }

    // ---------------------------------------------------------------------------------------------
    private final ByteBuffer buffer;

    private transient SocketAddress address;
}
