package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;

@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class _CalcMessage {

    // ------------------------------------------------------------------------------------ operator
    private static final int OFFSET_OPERATOR = 0;

    private static final int POSITION_OPERATOR = 0;

    private static final int INDEX_OPERATOR = 0;

    private static final int LENGTH_OPERATOR = _CalcOperator.NAME_BYTES;

    private static final int LIMIT_OPERATOR = POSITION_OPERATOR + LENGTH_OPERATOR;

    // ------------------------------------------------------------------------------------ operands
    private static final int LENGTH_OPERAND = Byte.BYTES;

    private static final int OFFSET_OPERAND1 = OFFSET_OPERATOR + LENGTH_OPERATOR;

    private static final int LENGTH_OPERAND1 = LENGTH_OPERAND;

    private static final int POSITION_OPERAND1 = LIMIT_OPERATOR;

    private static final int INDEX_OPERAND1 = LIMIT_OPERATOR;

    private static final int LIMIT_OPERAND1 = POSITION_OPERAND1 + LENGTH_OPERAND1;

    private static final int OFFSET_OPERAND2 = OFFSET_OPERAND1 + LENGTH_OPERAND1;

    private static final int LENGTH_OPERAND2 = LENGTH_OPERAND;

    private static final int POSITION_OPERAND2 = LIMIT_OPERAND1;

    private static final int INDEX_OPERAND2 = INDEX_OPERAND1 + LENGTH_OPERAND;

    private static final int LIMIT_OPERAND2 = POSITION_OPERAND2 + LENGTH_OPERAND2;

    // -------------------------------------------------------------------------------------- result
    private static final int OFFSET_RESULT = OFFSET_OPERAND2 + LENGTH_OPERAND2;

    private static final int LENGTH_RESULT = Byte.BYTES;

    private static final int POSITION_RESULT = LIMIT_OPERAND2;

    private static final int INDEX_RESULT = INDEX_OPERAND2 + LENGTH_OPERAND;

    private static final int LIMIT_RESULT = POSITION_RESULT + LENGTH_RESULT;

    // ---------------------------------------------------------------------------------------------
    static final int LENGTH_REQUEST = OFFSET_RESULT;

    static final int LENGTH_RESPONSE = LENGTH_RESULT;

    private static final int LENGTH = LENGTH_REQUEST + LENGTH_RESPONSE;

    // ------------------------------------------------------------------------------------ operator
    private static _CalcOperator operator(final ByteBuffer buffer) {
        final var bytes = new byte[LENGTH_OPERATOR];
        buffer.position(OFFSET_OPERATOR).limit(OFFSET_OPERAND1).get(bytes);
        assert !buffer.hasRemaining();
        return _CalcOperator.valueOf(new String(bytes, _CalcOperator.NAME_CHARSET));
    }

    private static ByteBuffer operator(final ByteBuffer buffer, final _CalcOperator operator) {
        return buffer
                .position(OFFSET_OPERATOR)
                .limit(OFFSET_OPERAND1)
                .put(operator.name().getBytes(_CalcOperator.NAME_CHARSET));
    }

    // ------------------------------------------------------------------------------------ operands
    private static byte operand(final ByteBuffer buffer, final int position) {
        return buffer
                .position(position)
                .limit(position + LENGTH_OPERAND1)
                .get();
    }

    private static ByteBuffer operand(final ByteBuffer buffer, final int position,
                                      final byte operand) {
        return buffer
                .position(position)
                .limit(position + LENGTH_OPERAND1)
                .put(operand);
    }

    private static byte operand1(final ByteBuffer buffer) {
        return operand(buffer, OFFSET_OPERAND1);
    }

    private static ByteBuffer operand1(final ByteBuffer buffer, final byte operand1) {
        return operand(buffer, OFFSET_OPERAND1, operand1);
    }

    private static byte operand2(final ByteBuffer buffer) {
        return operand(buffer, OFFSET_OPERAND2);
    }

    private static ByteBuffer operand2(final ByteBuffer buffer, final byte operand2) {
        return operand(buffer, OFFSET_OPERAND2, operand2);
    }

    // -------------------------------------------------------------------------------------- result
    private static byte result(final ByteBuffer buffer) {
        return buffer.limit(LENGTH).get(OFFSET_RESULT);
    }

    private static ByteBuffer result(final ByteBuffer buffer, final byte result) {
        return buffer.limit(LENGTH).put(OFFSET_RESULT, result);
    }

    // ---------------------------------------------------------------------------- operator/operand
    private static final List<_CalcOperator> CALC_OPERATOR_VALUES = List.of(_CalcOperator.values());

    static {
        assert !CALC_OPERATOR_VALUES.isEmpty();
    }

    private static _CalcOperator randomOperator() {
        return CALC_OPERATOR_VALUES.get(
                ThreadLocalRandom.current().nextInt(CALC_OPERATOR_VALUES.size())
        );
    }

    private static byte randomOperand() {
        return (byte) ThreadLocalRandom.current().nextInt(-9, 10);
    }

    // -------------------------------------------------------------------------------- array/buffer
    private static byte[] newArray() {
        return new byte[LENGTH];
    }

    private static ByteBuffer newBuffer() {
        return ByteBuffer.wrap(newArray());
    }

    static ByteBuffer newBufferForClient() {
        return operand2(
                operand1(
                        operator(
                                newBuffer(),
                                randomOperator()
                        ),
                        randomOperand()
                ),
                randomOperand()
        )
                .flip()
                ;
    }

    static byte[] newArrayForClient() {
        return newBufferForClient().array();
    }

    static ByteBuffer newBufferForServer() {
        return newBuffer().limit(LENGTH_REQUEST);
    }

    static byte[] newArrayForServer() {
        return newBufferForServer().array();
    }

    // --------------------------------------------------------------------------------------- apply
    private static <R> R apply(
            final ByteBuffer buffer,
            final Function<? super _CalcOperator,
                    ? extends IntFunction<
                            ? extends IntFunction<? extends R>>> function) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(operator(buffer))
                .apply(operand1(buffer))
                .apply(operand2(buffer))
                ;
    }

    private static <R> R apply(
            final byte[] array,
            final Function<? super _CalcOperator,
                    ? extends IntFunction<
                            ? extends IntFunction<? extends R>>> function) {
        Objects.requireNonNull(array, "buffer is null");
        Objects.requireNonNull(function, "function is null");
        return apply(ByteBuffer.wrap(array), function);
    }

    /**
     * Gets an operator and two operands from specified buffer, applies them, and puts the result on
     * the {@code buffer}.
     *
     * @param buffer the buffer.
     * @return given {@code buffer}.
     */
    static ByteBuffer apply(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        return apply(
                buffer,
                o -> l -> r -> result(buffer, (byte) o.applyAsInt(l, r))
        );
    }

    /**
     * Gets an operator and two operands from specified array, applies them, and set the result on
     * the array at {@value #OFFSET_RESULT}.
     *
     * @param array the array.
     * @return given {@code array}.
     */
    static byte[] apply(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return apply(ByteBuffer.wrap(array)).array();
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

    // ----------------------------------------------------------------------------------------- log

    /**
     * Logs out content of specified buffer.
     *
     * @param buffer the buffer whose content are logged out.
     */
    static void log(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        log.debug("{}({}, {}) = {}",
                  operator(buffer),
                  String.format("%1$+d", operand1(buffer)),
                  String.format("%1$+d", operand2(buffer)),
                  String.format("%1$+3d", result(buffer))
        );
    }

    /**
     * Logs out content of specified array.
     *
     * @param array the array whose content are logged out.
     */
    static void log(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        log(ByteBuffer.wrap(array));
    }

    // ---------------------------------------------------------------------------------------------
    static _CalcMessage newInstanceForServers() {
        return new _CalcMessage();
    }

    static _CalcMessage newInstanceForClients() {
        return newInstanceForServers()
                .operator(randomOperator())
                .operand1(randomOperand())
                .operand1(randomOperand());
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcMessage() {
        super();
        this.buffer = ByteBuffer.allocate(LENGTH);
    }

    // ------------------------------------------------------------------------------------ blocking
    _CalcMessage sendToServer(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(array(), 0, LENGTH_REQUEST);
        stream.flush();
        return this;
    }

    _CalcMessage receiveFromServer(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (stream.readNBytes(array(), LENGTH_REQUEST, LENGTH_RESPONSE) < LENGTH_RESPONSE) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    _CalcMessage receiveFromClient(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (stream.readNBytes(array(), 0, LENGTH_REQUEST) < LENGTH_REQUEST) {
            throw new EOFException("unexpected eof");
        }
        return this;
    }

    _CalcMessage sendToClient(final OutputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        stream.write(array(), LENGTH_REQUEST, LENGTH_RESPONSE);
        stream.flush();
        return this;
    }

    // -------------------------------------------------------------------------------- non-blocking
    _CalcMessage readyToSendToServer() {
        buffer.position(0).limit(LENGTH_REQUEST);
        return this;
    }

    _CalcMessage readyToReceiveFromServer() {
        buffer.position(LENGTH_REQUEST).limit(LENGTH);
        return this;
    }

    _CalcMessage readyToReceiveFromClient() {
        return readyToSendToServer();
    }

    _CalcMessage readyToSendToClient() {
        return readyToReceiveFromServer();
    }

    boolean send(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        channel.write(buffer);
        return buffer.hasRemaining();
    }

    boolean receive(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!buffer.hasRemaining()) {
            throw new IllegalStateException("buffer doesn't have remaining");
        }
        final var r = channel.read(buffer);
        if (r == -1) {
            throw new EOFException("unexpected eof");
        }
        return buffer.hasRemaining();
    }

    // -------------------------------------------------------------------------------- asynchronous

    // ---------------------------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------------------ operand1
    byte operand1() {
        return buffer.get(INDEX_OPERAND1);
    }

    _CalcMessage operand1(final byte operand1) {
        buffer.limit(INDEX_OPERAND2).put(INDEX_OPERAND1, operand1);
        return this;
    }

    // ------------------------------------------------------------------------------------ operand2
    byte operand2() {
        return buffer.get(INDEX_OPERAND2);
    }

    _CalcMessage operand2(final byte operand2) {
        buffer.limit(INDEX_RESULT).put(INDEX_OPERAND2, operand2);
        return this;
    }

    // -------------------------------------------------------------------------------------- result
    byte result() {
        return buffer.get(INDEX_RESULT);
    }

    _CalcMessage result(final byte result) {
        buffer.limit(LENGTH).put(INDEX_RESULT, result);
        return this;
    }

    // ------------------------------------------------------------------------------------- logging
    void log() {
        log.debug("{}({}, {}) = {}",
                  operator(),
                  String.format("%1$+d", operand1()),
                  String.format("%1$+d", operand2()),
                  String.format("%1$+3d", result())
        );
    }

    // -------------------------------------------------------------------------------------- buffer
    byte[] array() {
        return buffer.array();
    }

    // ---------------------------------------------------------------------------------------------
    private final ByteBuffer buffer;
}
