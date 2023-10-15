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

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

@Slf4j
final class _CalcUtils {

    // --------------------------------------------------------------------------- operator/operands
    private static final List<CalcOperator> CALC_OPERATOR_VALUES = List.of(CalcOperator.values());

    static {
        assert !CALC_OPERATOR_VALUES.isEmpty();
    }

    static CalcOperator getRandomOperator() {
        return CALC_OPERATOR_VALUES.get(
                ThreadLocalRandom.current().nextInt(CALC_OPERATOR_VALUES.size())
        );
    }

    static <R> R applyRandomOperator(final Function<? super CalcOperator, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getRandomOperator());
    }

    static void acceptRandomOperator(final Consumer<? super CalcOperator> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyRandomOperator(o -> {
            consumer.accept(o);
            return null;
        });
    }

    static int getRandomOperand() {
        return ThreadLocalRandom.current().nextInt(10);
    }

    static <R> R applyRandomOperand(final IntFunction<? extends R> operator) {
        Objects.requireNonNull(operator, "operator is null");
        return operator.apply(getRandomOperand());
    }

    static void acceptRandomOperand(final IntConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyRandomOperand(o -> {
            consumer.accept(o);
            return null;
        });
    }

    static <R> R applyRandomOperatorAndRandomOperands(
            final Function<? super CalcOperator,
                    ? extends IntFunction<
                            ? extends IntFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomOperator(
                o -> applyRandomOperand(
                        l -> applyRandomOperand(
                                r -> function.apply(o).apply(l).apply(r)
                        )
                )
        );
    }

    static void acceptRandomOperatorAndRandomOperands(
            final Function<? super CalcOperator,
                    ? extends IntFunction<
                            ? extends IntConsumer>> function) {
        Objects.requireNonNull(function, "function is null");
        applyRandomOperatorAndRandomOperands(o -> l -> r -> {
            function.apply(o).apply(l).accept(r);
            return null;
        });
    }

    static <R> R applyRandomOperatorRandomOperandsAlongWithResult(
            final Function<? super CalcOperator,
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends IntFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomOperatorAndRandomOperands(
                o -> l -> r ->
                        function.apply(o)
                                .apply(l)
                                .apply(r)
                                .apply(o.applyAsInt(l, r))
        );
    }

    static void acceptRandomOperatorRandomOperandsAlongWithResult(
            final Function<? super CalcOperator,
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends IntConsumer>>> function) {
        Objects.requireNonNull(function, "function is null");
        applyRandomOperatorRandomOperandsAlongWithResult(o -> l -> r -> v -> {
            function.apply(o).apply(r).apply(r).accept(v);
            return null;
        });
    }

    // -------------------------------------------------------------------------------- array/buffer
    private static byte[] newArray() {
        return new byte[
                CalcOperator.NAME_BYTES // operator
                + Integer.BYTES         // operand1
                + Integer.BYTES         // operand2
                + Integer.BYTES         // result
                ];
    }

    static byte[] newArrayForClient() {
        return _CalcUtils.applyRandomOperatorAndRandomOperands(
                o -> l -> r -> o.set(newArray(), l, r)
        );
    }

    static byte[] newArrayForServer() {
        return newArrayForClient();
    }

    private static ByteBuffer newBuffer() {
        return ByteBuffer.wrap(newArray());
    }

    static ByteBuffer newBufferForServer() {
        final var buffer = newBuffer();
        return buffer.limit(buffer.limit() - Integer.BYTES);
    }

    static ByteBuffer newBufferForClient() {
        return _CalcUtils.applyRandomOperatorAndRandomOperands(
                o -> l -> r -> o.put(newBuffer(), l, r).flip()
        );
    }

    // ----------------------------------------------------------------------------------------- log
    static void log(final CalcOperator operator, final int operand1, final int operand2,
                    final int result) {
        log.debug("{}({}, {}) = {}",
                  operator,
                  String.format("%1$+d", operand1),
                  String.format("%1$+d", operand2),
                  String.format("%1$+3d", result)
        );
    }

    static void log(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        CalcOperator.parse(
                buffer,
                o -> l -> r -> {
                    log(o, l, r, buffer.getInt());
                    return null;
                }
        );
    }

    static void log(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        log(ByteBuffer.wrap(array));
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcUtils() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
