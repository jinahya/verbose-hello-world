package com.github.jinahya.hello.misc.c03calc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

/**
 * Constants of operators for calculator.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://ko.wikipedia.org/wiki/%EC%82%AC%EC%B9%99%EC%97%B0%EC%82%B0">사칙연산</a>
 * (Wikipedia)
 * @see <a href="https://en.wikipedia.org/wiki/Arithmetic">Arithmetic</a> (Wikipedia)
 * @see <a href="https://ko.wikipedia.org/wiki/0%EC%9C%BC%EB%A1%9C_%EB%82%98%EB%88%84%EA%B8%B0">0으로
 * 나누기</a> (Wikipedia)
 * @see <a href="https://en.wikipedia.org/wiki/Division_by_zero">Division by zero</a> (Wikipedia)
 */
enum _CalcOperator implements IntBinaryOperator {

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
    static final int NAME_LENGTH = 3;

    static final int NAME_BYTES = NAME_LENGTH;

    static final Charset NAME_CHARSET = StandardCharsets.US_ASCII;

    static _CalcOperator valueOf(final byte[] nameBytes) {
        if (Objects.requireNonNull(nameBytes, "nameBytes is null").length != NAME_BYTES) {
            throw new IllegalArgumentException(
                    "nameBytes.length(" + nameBytes.length + ") != " + NAME_BYTES);
        }
        return valueOf(new String(nameBytes, NAME_CHARSET));
    }

    _CalcOperator() {
        // empty
    }

    byte[] toBytes() {
        return name().getBytes(NAME_CHARSET);
    }
}
