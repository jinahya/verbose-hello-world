package com.github.jinahya.hello.miscellaneous._java_lang_foreign;

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

import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.math.*;

import static java.lang.foreign.ValueLayout.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring how C's {@code <float.h>} and {@code <stdint.h>} numeric limits relate to
 * Java, fetching values from native through the
 * {@linkplain java.lang.foreign Foreign Function &amp; Memory} API.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("java.lang.foreign")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class __Java_Lang_Foreign__Test {

    /**
     * A nested class for C's {@code <stdint.h>} exact-width integer maxima, derived from the byte
     * sizes that FFM's {@link ValueLayout}s carry for the native {@code int8_t} / {@code int16_t} /
     * {@code int32_t} / {@code int64_t} types ({@code INTN_MAX == 2}<sup>{@code 8N-1}</sup>{@code -
     * 1}, {@code UINTN_MAX == 2}<sup>{@code 8N}</sup>{@code - 1}). Unlike {@code epsilon}
     * (obtainable from native {@code nextafter}), these macros have no linkable symbol nor library
     * function, so the size the layout exposes is the native handle on them.
     */
    @DisplayName("stdint.h")
    @Nested
    class Integral_Test {

        /**
         * Verifies that {@code int8_t}'s {@code INT8_MAX} equals {@link Byte#MAX_VALUE} ({@code 127})
         * and {@code UINT8_MAX} equals {@code 255}.
         */
        @DisplayName("int8_t: INT8_MAX / UINT8_MAX")
        @Test
        void __Int8() {
            assertEquals(BigInteger.valueOf(Byte.MAX_VALUE), signedMax(JAVA_BYTE)); // INT8_MAX
            assertEquals(BigInteger.valueOf(0xFFL), unsignedMax(JAVA_BYTE));        // UINT8_MAX
        }

        /**
         * Verifies that {@code int16_t}'s {@code INT16_MAX} equals {@link Short#MAX_VALUE}
         * ({@code 32767}) and {@code UINT16_MAX} equals {@code 65535}.
         */
        @DisplayName("int16_t: INT16_MAX / UINT16_MAX")
        @Test
        void __Int16() {
            assertEquals(BigInteger.valueOf(Short.MAX_VALUE), signedMax(JAVA_SHORT)); // INT16_MAX
            assertEquals(BigInteger.valueOf(0xFFFFL), unsignedMax(JAVA_SHORT));       // UINT16_MAX
        }

        /**
         * Verifies that {@code int32_t}'s {@code INT32_MAX} equals {@link Integer#MAX_VALUE} and
         * {@code UINT32_MAX} equals {@code 4294967295}.
         */
        @DisplayName("int32_t: INT32_MAX / UINT32_MAX")
        @Test
        void __Int32() {
            assertEquals(BigInteger.valueOf(Integer.MAX_VALUE), signedMax(JAVA_INT)); // INT32_MAX
            assertEquals(BigInteger.valueOf(0xFFFFFFFFL), unsignedMax(JAVA_INT));     // UINT32_MAX
        }

        /**
         * Verifies that {@code int64_t}'s {@code INT64_MAX} equals {@link Long#MAX_VALUE} and
         * {@code UINT64_MAX} equals {@code 18446744073709551615} (the unsigned view of all-ones).
         */
        @DisplayName("int64_t: INT64_MAX / UINT64_MAX")
        @Test
        void __Int64() {
            assertEquals(BigInteger.valueOf(Long.MAX_VALUE), signedMax(JAVA_LONG)); // INT64_MAX
            final var uint64Max = unsignedMax(JAVA_LONG);                          // UINT64_MAX
            log.debug("UINT64_MAX: {}", uint64Max);
            assertEquals(new BigInteger("18446744073709551615"), uint64Max);
            // cross-check against Java's unsigned-long view of all-ones (-1L)
            assertEquals(Long.toUnsignedString(-1L), uint64Max.toString());
        }

        /**
         * Returns {@code 2}<sup>{@code 8 * size - 1}</sup>{@code - 1}, the maximum of a signed
         * integer occupying the given layout's byte size.
         *
         * @param layout the value layout whose {@linkplain ValueLayout#byteSize() byte size} sets
         *               the width.
         * @return the signed maximum (e.g. {@code INTN_MAX}).
         */
        private static BigInteger signedMax(final ValueLayout layout) {
            return BigInteger.TWO.pow((int) layout.byteSize() * 8 - 1).subtract(BigInteger.ONE);
        }

        /**
         * Returns {@code 2}<sup>{@code 8 * size}</sup>{@code - 1}, the maximum of an unsigned
         * integer occupying the given layout's byte size.
         *
         * @param layout the value layout whose {@linkplain ValueLayout#byteSize() byte size} sets
         *               the width.
         * @return the unsigned maximum (e.g. {@code UINTN_MAX}).
         */
        private static BigInteger unsignedMax(final ValueLayout layout) {
            return BigInteger.TWO.pow((int) layout.byteSize() * 8).subtract(BigInteger.ONE);
        }
    }

    /**
     * A nested class for C's {@code <float.h>} epsilon, fetched from native {@code libm}.
     */
    @DisplayName("float.h")
    @Nested
    class FloatingPoint_Test {

        /**
         * Verifies that the C epsilon — fetched from native by downcalling {@code nextafterf} /
         * {@code nextafter} (since {@code FLT_EPSILON} / {@code DBL_EPSILON} are macros with no
         * linkable symbol) and computing {@code nextafter(1.0, 2.0) - 1.0}, the gap to the next
         * representable value — equals Java's {@link Math#ulp(float) Math.ulp(1.0f)}
         * (2<sup>-23</sup>) and {@link Math#ulp(double) Math.ulp(1.0d)} (2<sup>-52</sup>). Java/FFM
         * has no {@code long double}, so C's {@code LDBL_EPSILON} has no counterpart here.
         *
         * @throws Throwable as required by {@link MethodHandle#invokeExact}.
         */
        @DisplayName("native nextafter(1.0, 2.0) - 1.0 == (FLT|DBL)_EPSILON == ulp(1.0)")
        @Test
        void __Epsilon() throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var linker = Linker.nativeLinker();
            final var lookup = linker.defaultLookup(); // libSystem (macOS) / libc+libm (linux)

            // bind native nextafterf taking two floats and returning a float
            final var nextafterf = linker.downcallHandle(
                    lookup.find("nextafterf").orElseThrow(),
                    FunctionDescriptor.of(JAVA_FLOAT, JAVA_FLOAT, JAVA_FLOAT)
            );
            // bind native nextafter taking two doubles and returning a double
            final var nextafter = linker.downcallHandle(
                    lookup.find("nextafter").orElseThrow(),
                    FunctionDescriptor.of(JAVA_DOUBLE, JAVA_DOUBLE, JAVA_DOUBLE)
            );

            // -------------------------------------------------------------------------------- when
            // FLT_EPSILON is two to the power of minus 23 (about 1.19209290e-07)
            final var fltEpsilon = (float) nextafterf.invokeExact(1.0f, 2.0f) - 1.0f;
            log.debug("native FLT_EPSILON: {} ({})", fltEpsilon, Float.toHexString(fltEpsilon));
            // DBL_EPSILON is two to the power of minus 52 (about 2.2204460492503131e-16)
            final var dblEpsilon = (double) nextafter.invokeExact(1.0d, 2.0d) - 1.0d;
            log.debug("native DBL_EPSILON: {} ({})", dblEpsilon, Double.toHexString(dblEpsilon));

            // -------------------------------------------------------------------------------- then
            assertEquals(0x1.0p-23f, fltEpsilon);
            assertEquals(Math.ulp(1.0f), fltEpsilon);
            assertEquals(0x1.0p-52, dblEpsilon);
            assertEquals(Math.ulp(1.0d), dblEpsilon);
        }
    }
}
