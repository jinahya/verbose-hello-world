package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-basic
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
abstract class CalcMessageTest<T extends CalcMessage<T>> {

    // ------------------------------------------------------------------------------------ sequence
    @DisplayName("sequence")
    @Nested
    class SequenceTest {

        @DisplayName("getSequence()I")
        @Nested
        class GetSequenceTest {

            @Test
            void _Zero_New() {
                // ------------------------------------------------------------------------------- given
                final var instance = newInstance();
                // -------------------------------------------------------------------------------- when
                final var result = instance.sequence();
                // -------------------------------------------------------------------------------- then
                Assertions.assertEquals(0, result);
            }
        }

        @DisplayName("setSequence(I)T")
        @Nested
        class SetSequenceTest {

            @DisplayName("setSequence(random)T")
            @Test
            void _DoesNotThrow_Random() {
                // --------------------------------------------------------------------------- given
                final var instance = newInstance();
                final var value = ThreadLocalRandom.current().nextInt();
                // ---------------------------------------------------------------------------- when
                final var result = Assertions.assertDoesNotThrow(() -> instance.sequence(value));
                // ---------------------------------------------------------------------------- then
                Assertions.assertEquals(instance, result);
                Assertions.assertEquals(value & 0xFF, instance.sequence());
            }
        }
    }

    // ------------------------------------------------------------------------------------ operator
    @Nested
    class OperatorTest {

        @DisplayName("getOperator()operator")
        @Nested
        class GetOperatorTest {

            @Test
            void _Null_New() {
                // --------------------------------------------------------------------------- given
                final var instance = newInstance();
                // ---------------------------------------------------------------------------- when
                final var result = instance.operator();
                // ---------------------------------------------------------------------------- then
                Assertions.assertNull(result);
            }
        }

        @DisplayName("setOperator(operator)")
        @Nested
        class SetOperatorTest {

            @DisplayName("setOperator(null)")
            @Test
            void _DoesNotThrow_Null() {
                // --------------------------------------------------------------------------- given
                final var instance = newInstance();
                // ---------------------------------------------------------------------------- when
                final var result = Assertions.assertDoesNotThrow(
                        () -> instance.operator(null)
                );
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(instance, result);
                Assertions.assertNull(instance.operator());
            }

            @DisplayName("setOperator(!null)")
            @EnumSource(CalcOperator.class)
            @ParameterizedTest
            void _DoesNotThrow_NotNull(final CalcOperator operator) {
                // --------------------------------------------------------------------------- given
                final var instance = newInstance();
                // ---------------------------------------------------------------------------- when
                final var result = Assertions.assertDoesNotThrow(
                        () -> instance.operator(operator)
                );
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(instance, result);
                Assertions.assertSame(operator, instance.operator());
            }
        }
    }

    // ------------------------------------------------------------------------------------ operand1
    @DisplayName("operand1")
    @Nested
    class Operand1Test {

        @Test
        void getOperand1_0_New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            // -------------------------------------------------------------------------------- when
            final var result = instance.operand1();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(0, result);
        }

        @Test
        void setOperand1__New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            final var operand1 = ThreadLocalRandom.current().nextInt();
            // -------------------------------------------------------------------------------- when
            final var result = instance.operand1(operand1);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(instance, result);
            Assertions.assertEquals((operand1 << 28) >> 28, instance.operand1());
        }
    }

    // ------------------------------------------------------------------------------------ operand2
    @DisplayName("operand2")
    @Nested
    class Operand2Test {

        @Test
        void getOperand2_0_New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            // -------------------------------------------------------------------------------- when
            final var result = instance.operand2();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(0, result);
        }

        @Test
        void setOperand2__New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            final var operand2 = ThreadLocalRandom.current().nextInt();
            // -------------------------------------------------------------------------------- when
            final var result = instance.operand2(operand2);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(instance, result);
            Assertions.assertEquals((operand2 << 28) >> 28, instance.operand2());
        }
    }

    // ------------------------------------------------------------------------------------ result
    @DisplayName("result")
    @Nested
    class ResultTest {

        @Test
        void getResult_0_New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            // -------------------------------------------------------------------------------- when
            final var result = instance.result();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(0, result);
        }

        @Test
        void setResult__New() {
            // ------------------------------------------------------------------------------- given
            final var instance = newInstance();
            final var value = ThreadLocalRandom.current().nextInt();
            // -------------------------------------------------------------------------------- when
            final var result = instance.result(value);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(instance, result);
            Assertions.assertEquals((value << 24) >> 24, instance.result());
        }
    }

    // ---------------------------------------------------------------------------------------------
    CalcMessageTest(final Class<T> messageClass) {
        super();
        this.messageClass = messageClass;
    }

    // -------------------------------------------------------------------------------- messageClass
    T newInstance() {
        try {
            final var constructor = messageClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }
    }

    // ---------------------------------------------------------------------------------------------
    private final Class<T> messageClass;
}
