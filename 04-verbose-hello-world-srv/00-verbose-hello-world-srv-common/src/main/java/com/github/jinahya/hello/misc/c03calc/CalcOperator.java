package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;

/**
 * Operators for calculator.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://ko.wikipedia.org/wiki/%EC%82%AC%EC%B9%99%EC%97%B0%EC%82%B0">사칙연산</a>
 * (Wikipedia)
 * @see <a href="https://en.wikipedia.org/wiki/Arithmetic">Arithmetic</a> (Wikipedia)
 * @see <a href="https://ko.wikipedia.org/wiki/0%EC%9C%BC%EB%A1%9C_%EB%82%98%EB%88%84%EA%B8%B0">0으로
 * 나누기</a> (Wikipedia)
 * @see <a href="https://en.wikipedia.org/wiki/Division_by_zero">Division by zero</a> (Wikipedia)
 */
@Slf4j
enum CalcOperator implements IntBinaryOperator {

    ADD() {
        @Override
        public int applyAsInt(final int left, final int right) {
            return left + right;
        }
    },

    SUB() {
        @Override
        public int applyAsInt(final int left, final int right) {
            return left - right;
        }
    },

    MUL() {
        @Override
        public int applyAsInt(final int left, final int right) {
            return left * right;
        }
    },

    DIV() {
        @Override
        public int applyAsInt(final int left, final int right) {
            if (right == 0) {
                return 0;
            }
            return left / right;
        }
    };

    // ---------------------------------------------------------------------------------------------
    static final int NAME_BYTES = 3;

    private static final Charset NAME_CHARSET = StandardCharsets.US_ASCII;

    static {
        for (final var value : values()) {
            assert value.name().getBytes(NAME_CHARSET).length == NAME_BYTES;
        }
    }

    // ---------------------------------------------------------------------------------------------
    static CalcOperator valueOf(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        final byte[] bytes = new byte[NAME_BYTES];
        buffer.get(bytes); // BufferUnderflowException
        return valueOf(new String(bytes, NAME_CHARSET));
    }

    static <R> R parse(final ByteBuffer buffer,
                       final Function<? super CalcOperator,
                               ? extends IntFunction<
                                       ? extends IntFunction<? extends R>>> function) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(valueOf(buffer))
                .apply(buffer.getInt())
                .apply(buffer.getInt())
                ;
    }

    static <R> R parse(final byte[] array,
                       final Function<? super CalcOperator,
                               ? extends IntFunction<
                                       ? extends IntFunction<
                                               ? extends R>>> function) {
        Objects.requireNonNull(array, "array is null");
        return parse(ByteBuffer.wrap(array), function);
    }

    static ByteBuffer apply(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        final var result = parse(buffer, o -> l -> r -> o.applyAsInt(l, r));
        return buffer.putInt(buffer.position(), result);
    }

    static byte[] apply(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return apply(ByteBuffer.wrap(array)).array();
    }

    // ---------------------------------------------------------------------------------------------
    CalcOperator() {
        // empty
    }

    // ---------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    <T extends ByteBuffer> T put(final T buffer, final int operand1, final int operand2) {
        Objects.requireNonNull(buffer, "buffer is null");
        return (T) buffer
                .put(name().getBytes(NAME_CHARSET))
                .putInt(operand1)
                .putInt(operand2);
    }

    byte[] set(final byte[] array, final int operand1, final int operand2) {
        return put(ByteBuffer.wrap(array), operand1, operand2).array();
    }
}
