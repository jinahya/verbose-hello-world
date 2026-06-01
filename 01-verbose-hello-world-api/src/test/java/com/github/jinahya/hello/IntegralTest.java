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

import lombok.*;
import org.junit.jupiter.api.*;

@DisplayName("integral")
class IntegralTest {

    /**
     * Returns a string representing binary of the specified value, starting from the specified
     * higher bit index (inclusive) to the specified lower bit index (inclusive).
     *
     * @param i the int value whose bit binary is printed.
     * @param h the higher bit index (inclusive) which should be less than or equal to {@code 31}
     *          and greater than the {@code l}.
     * @param l the lower bit index (inclusive) which should be greater than or equal to {@code 0}
     *          and less than the {@code h}.
     * @return a string representing bit
     */
    static String printBits(int i, final int h, final int l) {
        assert h < Integer.SIZE;
        assert l >= 0 && l < h;
        i >>= l;
        final StringBuilder builder = new StringBuilder();
        for (int b = l; b <= h; b++, i >>= 1) {
            builder.append(i & 1);
        }
        return builder.reverse().toString();
    }

    /**
     * Returns a string representing binary of the specified value, starting from the specified
     * higher bit index (inclusive) to the specified lower bit index (inclusive).
     *
     * @param l  the long value whose bit binary is printed.
     * @param h  the higher bit index (inclusive) which should be less than or equal to {@code 63}
     *           and greater than the {@code lo}.
     * @param lo the lower bit index (inclusive) which should be greater than or equal to {@code 0}
     *           and less than the {@code h}.
     * @return a string representing bit
     */
    static String printBits(long l, final int h, final int lo) {
        assert h < Long.SIZE;
        assert lo >= 0 && lo < h;
        l >>= lo;
        final StringBuilder builder = new StringBuilder();
        for (int b = lo; b <= h; b++, l >>= 1) {
            builder.append(l & 1);
        }
        return builder.reverse().toString();
    }

    // -----------------------------------------------------------------------------------------------------------------

    static String printBits(final byte b) {
        return printBits(b, 7, 0);
    }

    static String printBits(final short s) {
        return printBits(s, 15, 8) + "_" + printBits(s, 7, 0);
    }

    static String printBits(final int i) {
        return printBits(i, 31, 24)
               + "_" + printBits(i, 23, 16)
               + "_" + printBits(i, 15, 8)
               + "_" + printBits(i, 7, 0);
    }

    static String printBits(final long v) {
        return printBits(v, 63, 56)
               + "_" + printBits(v, 55, 48)
               + "_" + printBits(v, 47, 40)
               + "_" + printBits(v, 39, 32)
               + "_" + printBits(v, 31, 24)
               + "_" + printBits(v, 23, 16)
               + "_" + printBits(v, 15, 8)
               + "_" + printBits(v, 7, 0);
    }

    static String printBits(final char c) {
        return printBits(c, 15, 8) + "_" + printBits(c, 7, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    static void printf(final String name, final byte value) {
        System.out.printf("%-20s: %s (%+4d, 0x%02X)%n",
                          name, printBits(value), value, Byte.toUnsignedInt(value));
    }

    static void printf(final String name, final short value) {
        System.out.printf("%-20s: %s (%+6d, 0x%04X)%n",
                          name, printBits(value), value, Short.toUnsignedInt(value));
    }

    static void printf(final String name, final int value) {
        System.out.printf("%-20s: %s (%+11d, 0x%08X)%n",
                          name, printBits(value), value, value);
    }

    static void printf(final String name, final long value) {
        System.out.printf("%-20s: %s (%+20d, 0x%016X)%n",
                          name, printBits(value), value, value);
    }

    static void printf(final String name, final char value) {
        System.out.printf("%-20s: %s (%+6d, U+%04X, '%c')%n",
                          name, printBits(value), (int) value, (int) value, value);
    }

    // =================================================================================================================

    @DisplayName("byte")
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ByteTests {

        @DisplayName("should print bit patterns of representative <byte> values")
        @Test
        void values() {
            printf("Byte.MIN_VALUE", Byte.MIN_VALUE);
            printf("     (byte) -1", (byte) -1);
            printf("     (byte)  0", (byte) 0);
            printf("     (byte) +1", (byte) +1);
            printf("Byte.MAX_VALUE", Byte.MAX_VALUE);
        }
    }

    @DisplayName("short")
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class ShortTests {

        @DisplayName("should print bit patterns of representative <short> values")
        @Test
        void values() {
            printf("Short.MIN_VALUE", Short.MIN_VALUE);
            printf("    (short) -1", (short) -1);
            printf("    (short)  0", (short) 0);
            printf("    (short) +1", (short) +1);
            printf("Short.MAX_VALUE", Short.MAX_VALUE);
        }
    }

    @DisplayName("int")
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class IntTests {

        @DisplayName("should print bit patterns of representative <int> values")
        @Test
        void values() {
            printf("Integer.MIN_VALUE", Integer.MIN_VALUE);
            printf("               -1", -1);
            printf("                0", 0);
            printf("               +1", +1);
            printf("Integer.MAX_VALUE", Integer.MAX_VALUE);
        }
    }

    @DisplayName("long")
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class LongTests {

        @DisplayName("should print bit patterns of representative <long> values")
        @Test
        void values() {
            printf("Long.MIN_VALUE", Long.MIN_VALUE);
            printf("           -1L", -1L);
            printf("            0L", 0L);
            printf("           +1L", +1L);
            printf("Long.MAX_VALUE", Long.MAX_VALUE);
        }
    }

    @DisplayName("char")
    @Nested
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class CharTests {

        @DisplayName("should print bit patterns of representative <char> values")
        @Test
        void values() {
            printf("Character.MIN_VALUE", Character.MIN_VALUE);
            printf("              ' '", ' ');
            printf("              '0'", '0');
            printf("              'A'", 'A');
            printf("              'a'", 'a');
            printf("Character.MAX_VALUE", Character.MAX_VALUE);
        }
    }
}
