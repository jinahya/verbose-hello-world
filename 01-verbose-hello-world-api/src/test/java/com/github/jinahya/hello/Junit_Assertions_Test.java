package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit assertions")
class Junit_Assertions_Test {

    @DisplayName("assertTrue / assertFalse")
    @Nested
    class AssertTrue_Test {

        @DisplayName("should pass when <assertTrue> is invoked with <true>")
        @Test
        void assertTrue__True() {
            final var condition = true;
            Assertions.assertTrue(condition); // Passes
        }

        @DisplayName("should fail when <assertTrue> is invoked with <false>")
        @Disabled
        @Test
        void assertTrue__False() {
            final var condition = false;
            Assertions.assertTrue(condition); // Fails
        }

        @DisplayName("should fail when <assertFalse> is invoked with <true>")
        @Disabled
        @Test
        void assertFalse__True() {
            final var condition = true;
            Assertions.assertFalse(condition); // Fails
        }

        @DisplayName("should pass when <assertFalse> is invoked with <false>")
        @Test
        void assertFalse__False() {
            final var condition = false;
            Assertions.assertFalse(condition); // Passes
        }
    }

    @DisplayName("assertNull / assertNotNull")
    @Nested
    class AssertNull_Test {

        @DisplayName(
                "should pass <assertNull> for <null> and <assertNotNull> for a non-<null> value")
        @Test
        void __() {
            {
                Assertions.assertNull(null);
                try {
                    Assertions.assertNotNull(null);
                } catch (final AssertionError ae) {
                    // expected
                }
            }
            {
                final Supplier<Object> supplier = Object::new;
                try {
                    Assertions.assertNull(supplier.get());
                } catch (final AssertionError ae) {
                    // expected
                }
                Assertions.assertNotNull(supplier.get());
            }
        }
    }

    @DisplayName("assertThrows / assertDoesNotThrow")
    @Nested
    class AssertThrows_Test {

        @DisplayName("""
                should capture a <NumberFormatException>
                with <assertThrows> and <assertThrowsExactly>""")
        @Test
        void assertThrows__() {
            {
                final ToIntFunction<String> parser = Integer::parseInt;
                Assertions.assertThrows(
                        Throwable.class,
                        () -> parser.applyAsInt(null)
                );
                Assertions.assertDoesNotThrow(
                        () -> parser.applyAsInt("0")
                );
                Assertions.assertThrowsExactly(
                        NumberFormatException.class,
                        () -> parser.applyAsInt(null)
                );
            }
        }

        @DisplayName("should pass when no exception is thrown")
        @Test
        void assertDoesNotThrow__() {
            final IntUnaryOperator fx = a -> a + 1;
        }
    }

    @DisplayName("assertSame / assertNotSame")
    @Nested
    class AssertSame_Test {

        @DisplayName("should treat <null> as the same reference as <null>")
        @Test
        void __Null() {
            assert null == null;
            Assertions.assertSame(null, null);
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotSame(null, null);
            });
        }

        @DisplayName("should treat the same reference as the same in <assertSame>")
        @Test
        void __Self() {
            final Object self = new Object();
            assert self == self;
            Assertions.assertSame(self, self);
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotSame(self, self);
            });
        }

        @DisplayName("should treat aliased references as the same in <assertSame>")
        @Test
        void __Same() {
            final Object expected = new Object();
            final Object actual = expected;
            assert actual == expected;
            Assertions.assertSame(expected, actual);
        }

        @DisplayName("should fail <assertSame> for distinct <Object> references")
        @Test
        void __Other() {
            final Object expected = new Object();
            final Object actual = new Object();
            assert actual != expected;
            assertThrows(AssertionError.class, () -> {
                Assertions.assertSame(expected, actual); // Fails
            });
        }
    }

    @DisplayName("assertEquals / assertNotEquals")
    @Nested
    class AssertEquals_Test {

        @DisplayName("should treat <null> as equal to <null> in <assertEquals>")
        @Test
        void __Null() {
            assert java.util.Objects.equals(null, null); // (a == b) || (a != null && a.equals(b))
            // then
            Assertions.assertEquals((Object) null, null);        // Passes
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotEquals((Object) null, null); // Fails
            });
        }

        @DisplayName("should treat the same reference as equal in <assertEquals>")
        @Test
        void __Self() {
            {
                final var self = new Object();
                assert self.equals(self); // (this == obj)
                // then
                Assertions.assertEquals(self, self);
                assertThrows(AssertionError.class, () -> {
                    Assertions.assertNotEquals(self, self);
                });
            }
            {
                final var expected = new Object();
                final var actual = expected;
                assert actual.equals(expected);
                Assertions.assertEquals(expected, actual);
            }
        }

        @DisplayName("should fail <assertEquals> for distinct <Object> instances")
        @Test
        void __Other() {
            final var expected = new Object();
            final var actual = new Object();
            assert actual != expected;
            assert !java.util.Objects.equals(actual, expected);
            assertThrows(AssertionError.class, () -> {
                Assertions.assertEquals(expected, actual); // Fails
            });
        }

        @DisplayName("should treat interned and <new> <String> literals as equal in <assertEquals>")
        @Test
        void __John() {
            {
                final var john1 = "John";
                final var john2 = "Jo" + "hn";
                assert john2 == john1;
                Assertions.assertSame(john1, john2);
                assert java.util.Objects.equals(john2, john1);
                Assertions.assertEquals(john1, john2);
            }
            {
                final var john1 = new String("John");
                final var john2 = new String("Jo" + "hn");
                assert john2 != john1;
                Assertions.assertNotSame(john1, john2);
                assert java.util.Objects.equals(john2, john1);
                Assertions.assertEquals(john1, john2);
            }
        }

        @DisplayName("""
                should treat equal <int> values as equal in <assertEquals>
                across the <Integer> cache boundary""")
        @Test
        void __Int() {
            {
                final var int1 = 127;
                final var int2 = 127;
                Assertions.assertSame(int2, int1);
                Assertions.assertEquals(int2, int1);
            }
            {
                final var int1 = 128;
                final var int2 = 128;
                Assertions.assertNotSame(int2, int1);
                Assertions.assertEquals(int2, int1);
            }
        }

        @DisplayName("""
                should treat equal <Integer> values as equal in <assertEquals>
                across the <Integer> cache boundary""")
        @Test
        void __Integer() {
            {
                final int int1 = -128;
                final int int2 = -128;
                Assertions.assertSame(int2, int1);
                Assertions.assertEquals(int2, int1);
            }
            {
                final var int1 = 127;
                final var int2 = Integer.valueOf(127);
                Assertions.assertSame(int2, int1);
                Assertions.assertEquals(int2, int1);
            }
            {
                final var int1 = 128;
                final var int2 = Integer.valueOf(128);
                Assertions.assertNotSame(int2, int1);
                Assertions.assertEquals(int2, int1);
            }
            {
                final var integer1 = Integer.valueOf(127);
                final var integer2 = Integer.valueOf(127);
                assert integer2 == integer1;
                Assertions.assertSame(integer2, integer1);
                assert integer2.equals(integer1);
                Assertions.assertEquals(integer2, integer1);
            }
            {
                final var integer1 = Integer.valueOf(128);
                final var integer2 = Integer.valueOf(128);
                assert integer2 != integer1;
                Assertions.assertNotSame(integer2, integer1);
                assert integer2.equals(integer1);
                Assertions.assertEquals(integer2, integer1);
            }
        }
    }
}
