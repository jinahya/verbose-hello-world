package com.github.jinahya.hello.miscellaneous._java_lang;

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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring the machine {@code epsilon} of {@link Float} and {@link Double} &mdash; the
 * {@linkplain Math#ulp(double) ulp} of {@code 1.0}, equal to {@code 2}<sup>{@code 1 -
 * PRECISION}</sup>, the gap between {@code 1.0} and the next representable value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("java.lang")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
final class __Java_Lang__Test {

    @DisplayName("Float")
    @Nested
    class Float_Test {

        /**
         * Verifies that the {@code float} machine epsilon &mdash; computed by halving until it no
         * longer perturbs {@code 1.0f} &mdash; equals {@link Math#ulp(float) Math.ulp(1.0f)},
         * {@code 0x1.0p-23f}, and {@code 2}<sup>{@code 1 - }{@link Float#PRECISION}</sup> (i.e. C's
         * {@code FLT_EPSILON}).
         */
        @DisplayName("machine epsilon == ulp(1.0f) == 2^-23")
        @Test
        void __MachineEpsilon() {
            var epsilon = 1.0f;
            while (1.0f + epsilon / 2.0f != 1.0f) {
                epsilon /= 2.0f;
            }
            log.debug("float machine epsilon: {} ({})", epsilon, Float.toHexString(epsilon));
            assertEquals(Math.ulp(1.0f), epsilon);
            assertEquals(0x1.0p-23f, epsilon);
            assertEquals(Math.scalb(1.0f, 1 - Float.PRECISION), epsilon);
        }
    }

    @DisplayName("Double")
    @Nested
    class Double_Test {

        /**
         * Verifies that the {@code double} machine epsilon &mdash; computed by halving until it no
         * longer perturbs {@code 1.0d} &mdash; equals {@link Math#ulp(double) Math.ulp(1.0d)},
         * {@code 0x1.0p-52}, and {@code 2}<sup>{@code 1 - }{@link Double#PRECISION}</sup> (i.e. C's
         * {@code DBL_EPSILON}).
         */
        @DisplayName("machine epsilon == ulp(1.0d) == 2^-52")
        @Test
        void __MachineEpsilon() {
            var epsilon = 1.0d;
            while (1.0d + epsilon / 2.0d != 1.0d) {
                epsilon /= 2.0d;
            }
            log.debug("double machine epsilon: {} ({})", epsilon, Double.toHexString(epsilon));
            assertEquals(Math.ulp(1.0d), epsilon);
            assertEquals(0x1.0p-52, epsilon);
            assertEquals(Math.scalb(1.0d, 1 - Double.PRECISION), epsilon);
        }
    }
}
