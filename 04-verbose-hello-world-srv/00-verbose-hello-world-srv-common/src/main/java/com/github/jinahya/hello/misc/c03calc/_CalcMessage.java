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
import java.util.function.Function;

@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class _CalcMessage {

    // ------------------------------------------------------------------------------------ operator
    private static final int INDEX_OPERATOR = 0;

    private static final int LIMIT_OPERATOR = INDEX_OPERATOR + _CalcOperator.BYTES;

    // ------------------------------------------------------------------------------------ operand1
    private static final int INDEX_OPERAND1 = LIMIT_OPERATOR;

    private static final int LIMIT_OPERAND1 = INDEX_OPERAND1 + Byte.BYTES;

    // ------------------------------------------------------------------------------------ operand2
    private static final int INDEX_OPERAND2 = LIMIT_OPERAND1;

    private static final int LIMIT_OPERAND2 = INDEX_OPERAND2 + Byte.BYTES;

    // -------------------------------------------------------------------------------------- result
    private static final int INDEX_RESULT = LIMIT_OPERAND2;

    private static final int LIMIT_RESULT = INDEX_RESULT + Byte.BYTES;

    // ---------------------------------------------------------------------------------------------
    static final int LENGTH_REQUEST = LIMIT_OPERAND2;

    static final int LENGTH_RESPONSE = LIMIT_RESULT;

    private static final int LENGTH = LENGTH_REQUEST + LENGTH_RESPONSE;

    // ------------------------------------------------------------------------------------ operator
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

    /**
     * Creates a new instance whose {@code operator}, {@code operand1}, and {@code operand2} are
     * initialized with random values, and is ready to be sent to a server.
     *
     * @return a new instance with {@code operator}, {@code operand1}, and {@code operand2}.
     */
    static _CalcMessage newInstanceForClient() {
        return new _CalcMessage()
                .operator(randomOperator())
                .operand1(randomOperand())
                .operand2(randomOperand())
                .readyToSendRequest();
    }

    /**
     * Creates a new instance ready to be received from a client.
     *
     * @return a new instance.
     */
    static _CalcMessage newInstanceForServer() {
        return new _CalcMessage()
                .readyToReceiveRequest();
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcMessage() {
        super();
        this.buffer = ByteBuffer.allocate(LENGTH);
    }

    @Override
    public String toString() {
        return super.toString() + '{'
               + "message=" + buffer
               + ",address=" + address +
               +'}';
    }

    // -------------------------------------------------------------------------------- tcp/blocking

    /**
     * Writes  {@code operator}, {@code operand1}, and {@code operand2} to specified output stream,
     * and flushes the stream.
     *
     * @param stream the output stream.
     * @return this message.
     * @throws IOException if an I/O error occurs.
     */
    _CalcMessage sendRequest(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(
                buffer.array(), // <b>
                0,              // <off>
                LENGTH_REQUEST  // <len>
        );
        stream.flush();
        return this;
    }

    /**
     * Reads  {@code result} from specified input stream.
     *
     * @param stream the input stream.
     * @return this message.
     * @throws IOException if an I/O error occurs.
     */
    _CalcMessage receiveResult(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var r = stream.readNBytes(
                buffer.array(), // <b>
                LENGTH_REQUEST, // <off>
                LENGTH_RESPONSE // <len>
        );
        if (r < LENGTH_RESPONSE) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    /**
     * Reads  {@code operator}, {@code operand1}, and {@code operand2} from specified input stream,
     * and flushes the stream.
     *
     * @param stream the input stream.
     * @return this message.
     * @throws IOException if an I/O error occurs.
     */
    _CalcMessage receiveRequest(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var r = stream.readNBytes(
                buffer.array(), // <b>
                0,              // <off>
                LENGTH_REQUEST  // <len>
        );
        if (r < LENGTH_REQUEST) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    /**
     * Writes  {@code result} to specified output stream, and flushes the stream.
     *
     * @param stream the output stream.
     * @return this message.
     * @throws IOException if an I/O error occurs.
     */
    _CalcMessage sendResult(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(
                buffer.array(), // <b>
                LENGTH_REQUEST, // <off>
                LENGTH_RESPONSE // <len>
        );
        stream.flush();
        return this;
    }

    // ---------------------------------------------------------------------------- tcp/non-blocking
    _CalcMessage write(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        final int w = channel.write(buffer);
        assert w >= 0;
        return this;
    }

    _CalcMessage read(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        final var r = channel.read(buffer);
        assert r >= -1;
        if (r == -1) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    // ---------------------------------------------------------------------------- tcp/asynchronous
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

    // -------------------------------------------------------------------------------- udp/blocking
    _CalcMessage sendRequest(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        socket.send(new DatagramPacket(buffer.array(), LENGTH_REQUEST, _CalcConstants.ADDR));
        return this;
    }

    _CalcMessage receiveResult(final DatagramSocket socket) throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        final var packet = new DatagramPacket(buffer.array(), LENGTH_REQUEST, LENGTH_RESPONSE);
        socket.receive(packet);
        address = packet.getSocketAddress();
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

    // ---------------------------------------------------------------------------- udp/non-blocking
    _CalcMessage sendRequest(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var w = channel.send(buffer, _CalcConstants.ADDR);
        assert w == LENGTH_REQUEST;
        return this;
    }

    _CalcMessage receiveResult(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        address = channel.receive(buffer);
        return this;
    }

    _CalcMessage receiveRequest(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        address = channel.receive(buffer);
        return this;
    }

    _CalcMessage sendResult(final DatagramChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (address == null) {
            throw new IllegalStateException("address is currently not set");
        }
        final var w = channel.send(buffer, address);
        assert w == LENGTH_RESPONSE;
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
        final var nameBytes = new byte[_CalcOperator.BYTES];
        buffer.limit(LIMIT_OPERATOR).get(INDEX_OPERATOR, nameBytes);
        return _CalcOperator.valueOf(nameBytes);
    }

    _CalcMessage operator(final _CalcOperator operator) {
        Objects.requireNonNull(operator, "operator is null");
        buffer.limit(LIMIT_OPERATOR).put(INDEX_OPERATOR, operator.bytes());
        return this;
    }

    // ------------------------------------------------------------------------------------ operand1
    byte operand1() {
        return buffer.limit(LIMIT_OPERAND1).get(INDEX_OPERAND1);
    }

    _CalcMessage operand1(final byte operand1) {
        buffer.limit(LIMIT_OPERAND1).put(INDEX_OPERAND1, operand1);
        return this;
    }

    // ------------------------------------------------------------------------------------ operand2

    byte operand2() {
        return buffer.limit(LIMIT_OPERAND2).get(INDEX_OPERAND2);
    }

    _CalcMessage operand2(final byte operand2) {
        buffer.limit(LIMIT_OPERAND2).put(INDEX_OPERAND2, operand2);
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

    // ---------------------------------------------------------------------------------------------
    <R> R debug(
            final Function<? super ByteBuffer,
                    ? extends Function<? super SocketAddress,
                            ? extends R>> function) {
        return Objects.requireNonNull(function, "function is null")
                .apply(buffer.asReadOnlyBuffer())
                .apply(address);
    }

    // -------------------------------------------------------------------------------------- buffer

    /**
     * Returns the value of {@code buffer.hasRemaining()}.
     *
     * @return the value of {@code buffer.hasRemaining()}.
     */
    boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    private _CalcMessage readyToSendRequest() {
        buffer.position(0).limit(LENGTH_REQUEST);
        return this;
    }

    /**
     * Gets this message ready to receive the {@code result} from the server.
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
     * Gets this message ready to be sent the {@code result} to the client.
     *
     * @return this message.
     */
    _CalcMessage readyToSendResult() {
        return readyToReceiveResult();
    }

    // ------------------------------------------------------------------------------------- address

    // ---------------------------------------------------------------------------------------------
    private final ByteBuffer buffer;

    private SocketAddress address;
}
