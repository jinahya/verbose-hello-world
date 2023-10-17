package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;

@Slf4j
final class _CalcMessage {

    // ------------------------------------------------------------------------------------ operator
    private static final int OFFSET_OPERATOR = 0;

    private static final int LENGTH_OPERATOR = _CalcOperator.BYTES;

    // ------------------------------------------------------------------------------------ operands
    private static final int LENGTH_OPERAND = Byte.BYTES;

    private static final int OFFSET_OPERAND1 = OFFSET_OPERATOR + LENGTH_OPERATOR;

    private static final int LENGTH_OPERAND1 = LENGTH_OPERAND;

    private static final int OFFSET_OPERAND2 = OFFSET_OPERAND1 + LENGTH_OPERAND1;

    private static final int LENGTH_OPERAND2 = LENGTH_OPERAND;

    // -------------------------------------------------------------------------------------- result
    private static final int OFFSET_RESULT = OFFSET_OPERAND2 + LENGTH_OPERAND2;

    private static final int LENGTH_RESULT = Byte.BYTES;

    // ---------------------------------------------------------------------------------------------
    static final int LENGTH_REQUEST = OFFSET_RESULT;

    static final int LENGTH_RESPONSE = LENGTH_RESULT;

    private static final int LENGTH = LENGTH_REQUEST + LENGTH_RESPONSE;

    // ------------------------------------------------------------------------------------ operator
    private static _CalcOperator operator(final ByteBuffer buffer) {
        final var bytes = new byte[LENGTH_OPERATOR];
        buffer.position(OFFSET_OPERATOR).limit(OFFSET_OPERAND1).get(bytes);
        assert !buffer.hasRemaining();
        return _CalcOperator.valueOf(new String(bytes, _CalcOperator.CHARSET));
    }

    private static ByteBuffer operator(final ByteBuffer buffer, final _CalcOperator operator) {
        return buffer
                .position(OFFSET_OPERATOR)
                .limit(OFFSET_OPERAND1)
                .put(operator.name().getBytes(_CalcOperator.CHARSET));
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

    // ---------------------------------------------------------------------------------- applyAsync
    static CompletableFuture<ByteBuffer> applyAsync(
            final ByteBuffer buffer,
            final Executor executor) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(executor, "executor is null");
        return CompletableFuture.supplyAsync(() -> apply(buffer), executor);
    }

    static CompletableFuture<byte[]> applyAsync(final byte[] array, final Executor executor) {
        Objects.requireNonNull(array, "array is null");
        return applyAsync(ByteBuffer.wrap(array), executor).handle((b, t) -> b.array());
    }

    // ----------------------------------------------------------------------------------------- log
    static void log(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        log.debug("{}({}, {}) = {}",
                  operator(buffer),
                  String.format("%1$+d", operand1(buffer)),
                  String.format("%1$+d", operand2(buffer)),
                  String.format("%1$+3d", result(buffer))
        );
    }

    static void log(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        log(ByteBuffer.wrap(array));
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcMessage() {
        throw new AssertionError("instantiation is not allowed");
    }
}
