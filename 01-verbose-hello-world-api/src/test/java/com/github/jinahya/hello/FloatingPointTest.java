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

// Raw Bit Field	Official IEEE 754 Name	Common "Math" Name
// Sign	Sign	Sign
// Exponent	Biased Exponent	Characteristic
// Fraction	Trailing Significand	Mantissa
/**
 * A class for exploring the IEEE 754 bit patterns of {@code float} and {@code double} values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("floating point")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class FloatingPointTest {

    static String printBits(final float f) {
        final int bits = Float.floatToRawIntBits(f);
        return (bits >>> 31)
               + "_" + IntegralTest.printBits(bits, 30, 23)
               + "_" + IntegralTest.printBits(bits, 22, 0);
    }

    static String printBits(final double d) {
        final long bits = Double.doubleToRawLongBits(d);
        return (bits >>> 63)
               + "_" + IntegralTest.printBits(bits, 62, 52)
               + "_" + IntegralTest.printBits(bits, 51, 0);
    }

    static void printf(final String name, final float value) {
        System.out.printf("%s: %s (%+e)%n", name, printBits(value), value);
    }

    static void printf(final String name, final double value) {
        System.out.printf("%s: %s (%+e)%n", name, printBits(value), value);
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName(
            "should print bit patterns of positive and negative <zero> for <float> and <double>")
    @Test
    void Zeros() {
        {
            printf("+0.0f", +0.0f);
            printf("-0.0f", -0.0f);
        }
        {
            printf("+0.0d", +0.0d);
            printf("-0.0d", -0.0d);
        }
    }

    @DisplayName("should print bit patterns of <normal> <float> and <double> values")
    @Test
    void NormalNumbers() {
        {
            // smallest normal: sign=0/1, exponent=00000001 (MIN_EXPONENT=-126), fraction=000...000
            printf("    +Float.MIN_NORMAL", +Float.MIN_NORMAL);
            printf("    -Float.MIN_NORMAL", -Float.MIN_NORMAL);
            // one: sign=0/1, exponent=01111111 (biased 127, value 0), fraction=000...000
            printf("                +1.0f", +1.0f);
            printf("                -1.0f", -1.0f);
            // FLT_EPSILON = ulp(1.0f) = 2^-23: sign=0, exponent=01101000 (biased 127-23=104), fraction=000...000
            printf("FLT_EPSILON (float.h)", Math.ulp(1.0f));
            // largest normal: sign=0/1, exponent=11111110 (MAX_EXPONENT=127), fraction=111...111
            printf("     +Float.MAX_VALUE", +Float.MAX_VALUE);
            printf("     -Float.MAX_VALUE", -Float.MAX_VALUE);
        }
        {
            // smallest normal: sign=0/1, exponent=00000000001 (MIN_EXPONENT=-1022), fraction=000...000
            printf("   +Double.MIN_NORMAL", +Double.MIN_NORMAL);
            printf("   -Double.MIN_NORMAL", -Double.MIN_NORMAL);
            // one: sign=0/1, exponent=01111111111 (biased 1023, value 0), fraction=000...000
            printf("                +1.0d", +1.0d);
            printf("                -1.0d", -1.0d);
            // DBL_EPSILON = ulp(1.0) = 2^-52: sign=0, exponent=01111001011 (biased 1023-52=971), fraction=000...000
            printf("DBL_EPSILON (float.h)", Math.ulp(1.0d));
            // largest normal: sign=0/1, exponent=11111111110 (MAX_EXPONENT=1023), fraction=111...111
            printf("    +Double.MAX_VALUE", +Double.MAX_VALUE);
            printf("    -Double.MAX_VALUE", -Double.MAX_VALUE);
        }
    }

    @DisplayName("should print bit patterns of <subnormal> <float> and <double> values")
    @Test
    void SubnormalNumbers() {
        {
            // smallest subnormal: sign=0/1, exponent=00000000, fraction=000...001 (lower boundary: 0.0f where fraction is also 0)
            printf("                 +Float.MIN_VALUE", +Float.MIN_VALUE);
            printf("                 -Float.MIN_VALUE", -Float.MIN_VALUE);
            // largest subnormal: sign=0/1, exponent=00000000, fraction=111...111 (just below MIN_NORMAL)
            final float floatLargestSubnormal = Float.intBitsToFloat(0x007FFFFF);
            printf("+Float.intBitsToFloat(0x007FFFFF)", +floatLargestSubnormal);
            printf("-Float.intBitsToFloat(0x007FFFFF)", -floatLargestSubnormal);
        }
        {
            // smallest subnormal: sign=0/1, exponent=00000000000, fraction=000...001 (lower boundary: 0.0d where fraction is also 0)
            printf("                            +Double.MIN_VALUE", +Double.MIN_VALUE);
            printf("                            -Double.MIN_VALUE", -Double.MIN_VALUE);
            // largest subnormal: sign=0/1, exponent=00000000000, fraction=111...111 (just below MIN_NORMAL)
            final double doubleLargestSubnormal = Double.longBitsToDouble(0x000FFFFFFFFFFFFFL);
            printf("+Double.longBitsToDouble(0x000FFFFFFFFFFFFFL)", +doubleLargestSubnormal);
            printf("-Double.longBitsToDouble(0x000FFFFFFFFFFFFFL)", -doubleLargestSubnormal);
        }
    }

    @DisplayName("""
            should print bit patterns of positive and negative <infinity>
            for <float> and <double>""")
    @Test
    void Infinities() {
        {
            printf("Float.POSITIVE_INFINITY", Float.POSITIVE_INFINITY);
            printf("Float.NEGATIVE_INFINITY", Float.NEGATIVE_INFINITY);
        }
        {
            printf("Double.POSITIVE_INFINITY", Double.POSITIVE_INFINITY);
            printf("Double.NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY);
        }
    }

    @DisplayName("should recognize <quiet NaN> bit patterns as <NaN> for <float> and <double>")
    @Test
    void qNaNs() {
        {
            printf("         Float.NaN", Float.NaN);
            final float floatNaNSign0 = Float.intBitsToFloat(
                    0x7FC00000); // qNaN: sign=0, fraction MSB=1
            final float floatNaNSign1 = Float.intBitsToFloat(
                    0xFFC00000); // qNaN: sign=1, fraction MSB=1
            printf("float NaN (sign=0)", floatNaNSign0);
            printf("float NaN (sign=1)", floatNaNSign1);
            Assertions.assertTrue(Float.isNaN(floatNaNSign0));
            Assertions.assertTrue(Float.isNaN(floatNaNSign1));
        }
        {
            printf("         Double.NaN", Double.NaN);
            final double doubleNaNSign0 = Double.longBitsToDouble(
                    0x7FF8000000000000L); // qNaN: sign=0, fraction MSB=1
            final double doubleNaNSign1 = Double.longBitsToDouble(
                    0xFFF8000000000000L); // qNaN: sign=1, fraction MSB=1
            printf("double NaN (sign=0)", doubleNaNSign0);
            printf("double NaN (sign=1)", doubleNaNSign1);
            Assertions.assertTrue(Double.isNaN(doubleNaNSign0));
            Assertions.assertTrue(Double.isNaN(doubleNaNSign1));
        }
    }

    @DisplayName("should recognize <signaling NaN> bit patterns as <NaN> for <float> and <double>")
    @Test
    void sNaNs() {
        {
            // float sNaN: exponent=11111111, fraction MSB=0, at least one other bit non-zero
            final float sNaN0 = Float.intBitsToFloat(0x7F800001); // fraction=000...001 (bit 0)
            final float sNaN1 = Float.intBitsToFloat(0x7F800002); // fraction=000...010 (bit 1)
            final float sNaN2 = Float.intBitsToFloat(
                    0x7FA00000); // fraction=010...000 (bit 21 — just below quiet bit)
            final float sNaN3 = Float.intBitsToFloat(
                    0x7FBFFFFF); // fraction=011...111 (all but MSB)
            printf("float sNaN (0x7F800001)", sNaN0);
            printf("float sNaN (0x7F800002)", sNaN1);
            printf("float sNaN (0x7FA00000)", sNaN2);
            printf("float sNaN (0x7FBFFFFF)", sNaN3);
            Assertions.assertTrue(Float.isNaN(sNaN0));
            Assertions.assertTrue(Float.isNaN(sNaN1));
            Assertions.assertTrue(Float.isNaN(sNaN2));
            Assertions.assertTrue(Float.isNaN(sNaN3));
        }
        {
            // double sNaN: exponent=11111111111, fraction MSB=0, at least one other bit non-zero
            final double sNaN0 = Double.longBitsToDouble(
                    0x7FF0000000000001L); // fraction=000...001 (bit 0)
            final double sNaN1 = Double.longBitsToDouble(
                    0x7FF0000000000002L); // fraction=000...010 (bit 1)
            final double sNaN2 = Double.longBitsToDouble(
                    0x7FF4000000000000L); // fraction=010...000 (bit 50 — just below quiet bit)
            final double sNaN3 = Double.longBitsToDouble(
                    0x7FF7FFFFFFFFFFFFL); // fraction=011...111 (all but MSB)
            printf("double sNaN (0x7FF0000000000001L)", sNaN0);
            printf("double sNaN (0x7FF0000000000002L)", sNaN1);
            printf("double sNaN (0x7FF4000000000000L)", sNaN2);
            printf("double sNaN (0x7FF7FFFFFFFFFFFFL)", sNaN3);
            Assertions.assertTrue(Double.isNaN(sNaN0));
            Assertions.assertTrue(Double.isNaN(sNaN1));
            Assertions.assertTrue(Double.isNaN(sNaN2));
            Assertions.assertTrue(Double.isNaN(sNaN3));
        }
    }
}
