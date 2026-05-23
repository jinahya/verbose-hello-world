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

class Junit_Assertions_Test {

    @Nested
    class AssertTrue_Test {

        @Test
        void assertTrue__True() {
            final var condition = true;
            Assertions.assertTrue(condition); // Passes
        }

        @Disabled
        @Test
        void assertTrue__False() {
            final var condition = false;
            Assertions.assertTrue(condition); // Fails
        }

        @Disabled
        @Test
        void assertFalse__True() {
            final var condition = true;
            Assertions.assertFalse(condition); // Fails
        }

        @Test
        void assertFalse__False() {
            final var condition = false;
            Assertions.assertFalse(condition); // Passes
        }
    }

    @Nested
    class AssertNull_Test {

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

    @Nested
    class AssertThrows_Test {

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

        @Test
        void assertDoesNotThrow__() {
            final IntUnaryOperator fx = a -> a + 1;
        }
    }

    @Nested
    class AssertSame_Test {

        @Test
        void __Null() {
            assert null == null;
            Assertions.assertSame(null, null);
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotSame(null, null);
            });
        }

        @Test
        void __Self() {
            final Object self = new Object();
            assert self == self;
            Assertions.assertSame(self, self);
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotSame(self, self);
            });
        }

        @Test
        void __Same() {
            final Object expected = new Object();
            final Object actual = expected;
            assert actual == expected;
            Assertions.assertSame(expected, actual);
        }

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

    @Nested
    class AssertEquals_Test {

        @Test
        void __Null() {
            assert java.util.Objects.equals(null, null); // (a == b) || (a != null && a.equals(b))
            // then
            Assertions.assertEquals((Object) null, null);        // Passes
            assertThrows(AssertionError.class, () -> {
                Assertions.assertNotEquals((Object) null, null); // Fails
            });
        }

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
