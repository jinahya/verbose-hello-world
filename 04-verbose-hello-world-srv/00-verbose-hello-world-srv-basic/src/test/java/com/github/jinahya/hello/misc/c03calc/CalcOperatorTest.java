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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
class CalcOperatorTest {

    @DisplayName("NAME_BYTES")
    @Nested
    class NAME_BYTES_Test {

        @DisplayName("value.name().bytes(\"US_ASCII\").length == NAME_BYTES")
        @EnumSource(CalcOperator.class)
        @ParameterizedTest
        void _IsEqualToBytes_NameBytesLength(final CalcOperator value) {
            Assertions.assertEquals(
                    CalcOperator.NAME_LENGTH,
                    value.name().getBytes(StandardCharsets.US_ASCII).length
            );
        }
    }

    @DisplayName("randomValue()")
    @Nested
    class RandomValueTest {

        @Test
        void __() {
            final var randomValue = CalcOperator.randomValue();
            Assertions.assertNotNull(randomValue, "randomValue() should return non-null");
        }
    }

    @DisplayName("cachedValueOf()")
    @Nested
    class CachedValueOfTest {

        @EnumSource(CalcOperator.class)
        @ParameterizedTest
        void __(final CalcOperator value) {
            final var result = CalcOperator.cachedValueOf(value.name());
            Assertions.assertSame(value, result);
        }
    }

    @DisplayName("value.applyAsInt()")
    @Nested
    class ApplyAsIntTest {

        private static int randomOperands() {
            return ThreadLocalRandom.current().nextInt(
                    CalcOperator.MIN_OPERAND,
                    CalcOperator.MAX_OPERAND + 1
            );
        }

        private static <R> R applyOperands(
                final IntFunction<? extends IntFunction<? extends R>> function) {
            return function
                    .apply(randomOperands())
                    .apply(randomOperands())
                    ;
        }

        private static Stream<Arguments> operandsArgumentsStream() {
            return Stream.concat(
                    Stream.of(
                            ApplyAsIntTest.<Arguments>applyOperands(l -> r -> Arguments.of(l, 0))
                    ),
                    IntStream.rangeClosed(0, ThreadLocalRandom.current().nextInt(4))
                            .mapToObj(i -> applyOperands(l -> r -> Arguments.of(l, r)))
            );
        }

        private static Stream<Arguments> operatorAndOperandsArgumentsStream() {
            return Arrays.stream(CalcOperator.values()).flatMap(v -> {
                return operandsArgumentsStream().map(a -> {
                    return Arguments.of(
                            v,
                            a.get()[0],
                            a.get()[1]
                    );
                });
            });
        }

        @MethodSource({"operatorAndOperandsArgumentsStream"})
        @ParameterizedTest
        void __(final CalcOperator operator, final int left, final int right) {
            final var result = operator.applyAsInt(left, right);
            Assertions.assertTrue(
                    result >= CalcOperator.MIN_RESULT,
                    String.format("%s %d %d %d should GE %d", operator.name(), left, right, result,
                                  CalcOperator.MIN_RESULT));
            Assertions.assertTrue(
                    result <= CalcOperator.MAX_RESULT,
                    String.format("%s %d %d %d should LE %d", operator.name(), left, right, result,
                                  CalcOperator.MAX_OPERAND));
        }
    }
}
