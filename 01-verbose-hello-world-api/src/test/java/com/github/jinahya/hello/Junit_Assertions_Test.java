package com.github.jinahya.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
    class AssertSame_Test {

        @Test
        void __Null() {
            assert null == null;
            Assertions.assertSame(null, null);
            Assertions.assertThrows(AssertionError.class, () -> {
                Assertions.assertNotSame(null, null);
            });
        }

        @Test
        void __Self() {
            final Object self = new Object();
            assert self == self;
            Assertions.assertSame(self, self);
            Assertions.assertThrows(AssertionError.class, () -> {
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
            Assertions.assertThrows(AssertionError.class, () -> {
                Assertions.assertSame(expected, actual); // Fails
            });
        }
    }

    @Nested
    class AssertEquals_Test {

        @Test
        void __Null() {
            assert java.util.Objects.equals(null, null);
            // then
            Assertions.assertEquals((Object) null, null);
            Assertions.assertThrows(AssertionError.class, () -> {
                Assertions.assertNotEquals((Object) null, null);
            });
        }

        @Test
        void __Self() {
            final var self = new Object();
            assert self.equals(self);
            // when
            Assertions.assertEquals(self, self);
            Assertions.assertThrows(AssertionError.class, () -> {
                Assertions.assertNotEquals(self, self);
            });
        }

        @Test
        void __Equals() {
            final var expected = new Object();
            final var actual = expected;
            assert actual.equals(expected);
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void __Other() {
            final var expected = new Object();
            final var actual = new Object();
            assert actual != expected;
            assert !java.util.Objects.equals(actual, expected);
            Assertions.assertThrows(AssertionError.class, () -> {
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
