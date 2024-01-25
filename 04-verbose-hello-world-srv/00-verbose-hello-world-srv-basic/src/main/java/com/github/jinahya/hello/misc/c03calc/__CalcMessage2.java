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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class __CalcMessage2 {

    static final int BYTES = 5;

    static final int INDEX_OPERATOR = 0;

    private static final int INDEX_OPERAND = 3;

    static final int INDEX_RESULT = 4;

    // ------------------------------------------------------------------------------------- operand
    private static int operand1(final byte operand) {
        return (operand << 24) >> 28;
    }

    private static int operand2(final byte operand) {
        return (operand << 28) >> 28;
    }

    private static byte operand(final int operand1, final int operand2) {
        return (byte) ((((operand1) & 0xF) << 4) | (operand2 & 0xF));
    }

    // --------------------------------------------------------------------------------------- array
    private static _CalcOperator operator(final byte[] array, final int offset) {
        return _CalcOperator.valueOf(
                new String(Arrays.copyOf(array, offset + INDEX_OPERAND), StandardCharsets.US_ASCII)
        );
    }

    static _CalcOperator getOperator(final byte[] array) {
        return operator(array, 0);
    }

    private static byte[] operator(final byte[] array, final int offset,
                                   final _CalcOperator operator) {
        System.arraycopy(
                operator.name().getBytes(StandardCharsets.US_ASCII),
                0,
                array,
                offset + INDEX_OPERATOR,
                INDEX_OPERAND
        );
        return array;
    }

    static byte[] setOperator(final byte[] array, final _CalcOperator operator) {
        System.arraycopy(
                operator.name().getBytes(StandardCharsets.US_ASCII),
                0,
                array,
                INDEX_OPERATOR,
                INDEX_OPERAND
        );
        return array;
    }

    private static int operand1(final byte[] array, final int offset) {
        return operand1(array[offset + INDEX_OPERAND]);
    }

    static int getOperand1(final byte[] array) {
        return operand1(array, 0);
    }

    private static byte[] operand1(final byte[] array, final int offset, final int operand1) {
        array[offset + INDEX_OPERAND] = operand(operand1, operand2(array, offset));
        return array;
    }

    static byte[] setOperand1(final byte[] array, final int operand1) {
        return operand1(array, 0, operand1);
    }

    private static int operand2(final byte[] array, final int offset) {
        return operand2(array[offset + INDEX_OPERAND]);
    }

    static int getOperand2(final byte[] array) {
        return operand2(array, 0);
    }

    private static byte[] operand2(final byte[] array, final int offset, final int operand2) {
        array[offset + INDEX_OPERAND] = operand(operand1(array, offset), operand2);
        return array;
    }

    static byte[] setOperand2(final byte[] array, final int operand2) {
        return operand2(array, 0, operand2);
    }

    private static byte[] calculateResult(final byte[] array, final int offset) {
        final var operator = operator(array, offset);
        final var result = operator.applyAsInt(
                operand1(array, offset),
                operand2(array, offset)
        );
        array[offset + INDEX_RESULT] = (byte) result;
        return array;
    }

    static byte[] calculateResult(final byte[] array) {
        return calculateResult(array, 0);
    }

    private static int result(final byte[] array, final int offset) {
        return array[offset + INDEX_RESULT];
    }

    private static int getResult(final byte[] array) {
        return result(array, 0);
    }

    // -------------------------------------------------------------------------------------- buffer
    static _CalcOperator getOperator(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return operator(buffer.array(), buffer.arrayOffset());
        }
        final var dst = new byte[INDEX_OPERAND];
        buffer.limit(INDEX_OPERAND).position(INDEX_OPERATOR).get(dst);
        return _CalcOperator.valueOf(new String(dst, StandardCharsets.US_ASCII));
    }

    static ByteBuffer setOperator(final ByteBuffer buffer, final _CalcOperator operator) {
        if (buffer.hasArray()) {
            operator(buffer.array(), buffer.arrayOffset(), operator);
            return buffer;
        }
        final var src = operator.name().getBytes(StandardCharsets.US_ASCII);
        buffer.position(INDEX_OPERATOR)
                .put(src, INDEX_OPERATOR, INDEX_OPERAND);
        return buffer;
    }

    static int getOperand1(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return operand1(buffer.array(), buffer.arrayOffset());
        }
        return operand1(buffer.get(INDEX_OPERAND));
    }

    static ByteBuffer setOperand1(final ByteBuffer buffer, final int operand1) {
        if (buffer.hasArray()) {
            operand1(buffer.array(), buffer.arrayOffset(), operand1);
            return buffer;
        }
        final var operand = operand(operand1, getOperand2(buffer));
        return buffer.put(INDEX_OPERAND, operand);
    }

    static int getOperand2(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return operand2(buffer.array(), buffer.arrayOffset());
        }
        assert buffer.limit() == buffer.capacity();
        return operand2(buffer.get(INDEX_OPERAND));
    }

    static ByteBuffer setOperand2(final ByteBuffer buffer, final int operand2) {
        if (buffer.hasArray()) {
            operand2(buffer.array(), buffer.arrayOffset(), operand2);
            return buffer;
        }
        final var operand = operand(getOperand1(buffer), operand2);
        return buffer.put(INDEX_OPERAND, operand);
    }

    static ByteBuffer calculateResult(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            calculateResult(buffer.array(), buffer.arrayOffset());
            return buffer;
        }
        final var operator = getOperator(buffer);
        final var operand1 = getOperand1(buffer);
        final var operand2 = getOperand2(buffer);
        final var result = (byte) operator.applyAsInt(operand1, operand2);
        return buffer.put(INDEX_RESULT, result);
    }

    private static int getResult(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return result(buffer.array(), buffer.arrayOffset());
        }
        return buffer.get(INDEX_RESULT);
    }

    // ---------------------------------------------------------------------------------------------
    private static void log(final byte[] array, final int offset) {
        log.debug("{} {} {} {}",
                  operator(array, offset),
                  String.format("%+2d", operand1(array, offset)),
                  String.format("%+2d", operand2(array, offset)),
                  String.format("%+3d", result(array, offset))
        );
    }

    static void log(final byte[] array) {
        log(array, 0);
    }

    static void log(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            log(buffer.array(), buffer.arrayOffset());
            return;
        }
        log.debug("{} {} {} {}",
                  getOperator(buffer),
                  String.format("%+2d", getOperand1(buffer)),
                  String.format("%+2d", getOperand2(buffer)),
                  String.format("%+3d", getResult(buffer))
        );
    }

    // ---------------------------------------------------------------------------------------------
    private static byte[] randomize(final byte[] array, final int offset) {
        return
                operand2(
                        operand1(
                                operator(
                                        array,
                                        offset,
                                        _CalcOperator.randomValue()
                                ),
                                offset,
                                ThreadLocalRandom.current().nextInt()
                        ),
                        offset,
                        ThreadLocalRandom.current().nextInt()
                );
    }

    static byte[] randomize(final byte[] array) {
        return randomize(array, 0);
    }

    static byte[] newRandomizedArray() {
        return randomize(new byte[BYTES]);
    }

    static ByteBuffer randomize(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            randomize(buffer.array(), buffer.arrayOffset());
            return buffer;
        }
        return
                setOperand2(
                        setOperand1(
                                setOperator(
                                        buffer,
                                        _CalcOperator.randomValue()
                                ),
                                ThreadLocalRandom.current().nextInt()
                        ),
                        ThreadLocalRandom.current().nextInt()
                );
    }

    static ByteBuffer newRandomizedBuffer() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return ByteBuffer.wrap(newRandomizedArray());
        }
        return randomize(ByteBuffer.allocateDirect(BYTES));
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private __CalcMessage2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
