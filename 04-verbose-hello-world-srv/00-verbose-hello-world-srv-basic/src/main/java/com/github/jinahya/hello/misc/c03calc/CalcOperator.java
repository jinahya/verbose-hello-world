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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;

/**
 * Operators applied with two operands.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Math#floorDiv(int, int)
 * @see Math#floorMod(int, int)
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
    },

//    FIV() {
//        @Override
//        public int applyAsInt(final int left, final int right) {
//            if (right == 0) {
//                return 0;
//            }
//            return Math.floorDiv(left, right);
//        }
//    },

//    MOD() {
//        @Override
//        public int applyAsInt(final int left, final int right) {
//            if (right == 0) {
//                return 0;
//            }
//            return left % right;
//        }
//    },

//    FOD() {
//        @Override
//        public int applyAsInt(final int left, final int right) {
//            if (right == 0) {
//                return 0;
//            }
//            return Math.floorMod(left, right);
//        }
//    }
    ;

    // ---------------------------------------------------------------------------------------------

    /**
     * A cached result of {@link #values()}.
     */
    static final List<CalcOperator> VALUES = Arrays.asList(values());

    /**
     * Returns a random value.
     *
     * @return a random value.
     */
    static CalcOperator randomValue() {
        return VALUES.get(ThreadLocalRandom.current().nextInt(VALUES.size()));
    }

    private static final Map<String, CalcOperator> VALUES_BY_NAMES = VALUES.stream().collect(
            Collectors.toUnmodifiableMap(Enum::name, Function.identity())
    );

    /**
     * Returns a value whose {@link #name()} is equal to specified value.
     *
     * @param name the value of {@link #name()} to match.
     * @return the value whose {@link #name()} is equal to specified value; {@code null} if none
     * matched.
     */
    static CalcOperator cachedValueOf(final String name) {
        return VALUES_BY_NAMES.get(name);
    }

    // ---------------------------------------------------------------------------------------------
    static final int NAME_LENGTH = 3;

    // ---------------------------------------------------------------------------------------------

    static final int SIZE_OPERAND = Byte.SIZE >> 1;

    static final int MIN_OPERAND = Integer.MIN_VALUE >> (Integer.SIZE - SIZE_OPERAND);

    static final int MAX_OPERAND = Integer.MAX_VALUE >> (Integer.SIZE - SIZE_OPERAND);

    static final int MIN_RESULT = MIN_OPERAND * MAX_OPERAND;

    static final int MAX_RESULT = MIN_OPERAND * MIN_OPERAND;
}
