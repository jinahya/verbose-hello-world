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
enum _CalcOperator
        implements IntBinaryOperator {

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
    static final int LENGTH = 3;

    static final int BYTES = LENGTH;

    static final Charset CHARSET = StandardCharsets.US_ASCII;

    static _CalcOperator valueOf(final byte[] bytes) {
        if (Objects.requireNonNull(bytes, "bytes is null").length != BYTES) {
            throw new IllegalArgumentException("bytes.length(" + bytes.length + ") != " + BYTES);
        }
        return valueOf(new String(bytes, CHARSET));
    }

    _CalcOperator() {
        // empty
    }

    byte[] bytes() {
        return name().getBytes(CHARSET);
    }
}
