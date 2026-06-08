package com.github.jinahya.hello.api._java_util;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

/**
 * A class for exploring {@link HelloWorld#set(BitSet, int) set(bitset, index)} method with a real
 * {@link BitSet} and BitSet-based algorithms (Jaccard similarity, Hamming distance, Sieve of
 * Eratosthenes).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(bitset, index)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Set_BitSet_Index__Test
        extends HelloWorld__Test {

    @BeforeEach
    void __a() {
        Mockito.doAnswer(i -> {
            final var bitset = i.getArgument(0, BitSet.class);
            var index = i.getArgument(1, Integer.class);
            for (int b : HelloWorld__TestUtils.hello_world_byte_array()) {
                for (var j = 0; j < Byte.SIZE; j++) {
                    bitset.set(index++, (b & 1) == 1);
                    b >>>= 1;
                }
            }
            return bitset;
        }).when(service()).set(
                ArgumentMatchers.<BitSet>notNull(),
                ArgumentMatchers.intThat(v -> v >= 0)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#set(BitSet, int) set(bitset, index)} method sets the
     * {@code "hello, world"} bits into a fresh {@link BitSet} starting at index {@code 0}.
     */
    @DisplayName("should set <hello, world> bits into a fresh <BitSet>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var bitset = new BitSet();
        // ------------------------------------------------------------------------------------ when
        service().set(bitset, 0);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(95, bitset.length());
        Assertions.assertEquals(48, bitset.cardinality());
        Assertions.assertEquals(128, bitset.size());
    }

    /**
     * Demonstrates Jaccard similarity, {@code |A ∩ B| / |A ∪ B|}, computed with
     * {@link BitSet#and(BitSet)}, {@link BitSet#or(BitSet)}, and {@link BitSet#cardinality()}. Used
     * in deduplication, near-duplicate detection, and recommendation systems.
     */
    @DisplayName("Jaccard")
    @Nested
    class Jaccard_Test {

        /**
         * Verifies that the Jaccard similarity equals {@code 1.0} when two {@link BitSet}s set by
         * {@link HelloWorld#set(BitSet, int) set(bitset, index)} have identical placements.
         */
        @DisplayName("should compute <Jaccard = 1.0> when placements are identical")
        @Test
        void __identical() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 0);
            final var intersection = (BitSet) a.clone();
            intersection.and(b);
            final var union = (BitSet) a.clone();
            union.or(b);
            final double jaccard =
                    (double) intersection.cardinality() / union.cardinality();
            Assertions.assertEquals(1.0, jaccard);
        }

        /**
         * Verifies that the Jaccard similarity equals {@code 0.0} when two {@link BitSet}s set by
         * {@link HelloWorld#set(BitSet, int) set(bitset, index)} at non-overlapping offsets are
         * compared.
         */
        @DisplayName("should compute <Jaccard = 0.0> when placements are non-overlapping")
        @Test
        void __disjoint() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 96);
            final var intersection = (BitSet) a.clone();
            intersection.and(b);
            final var union = (BitSet) a.clone();
            union.or(b);
            final double jaccard =
                    (double) intersection.cardinality() / union.cardinality();
            Assertions.assertEquals(0.0, jaccard);
        }
    }

    /**
     * Demonstrates Hamming distance, the count of differing bits, computed with
     * {@link BitSet#xor(BitSet)} and {@link BitSet#cardinality()}. Used in error-correcting codes
     * (Hamming, BCH), locality-sensitive hashing, and SimHash-style fingerprinting.
     */
    @DisplayName("Hamming")
    @Nested
    class Hamming_Test {

        /**
         * Verifies that the Hamming distance equals {@code 0} when two {@link BitSet}s set by
         * {@link HelloWorld#set(BitSet, int) set(bitset, index)} have identical placements.
         */
        @DisplayName("should compute <distance = 0> when placements are identical")
        @Test
        void __identical() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 0);
            final var diff = (BitSet) a.clone();
            diff.xor(b);
            Assertions.assertEquals(0, diff.cardinality());
        }

        /**
         * Verifies that the Hamming distance equals {@code |A| + |B|} when two {@link BitSet}s
         * set by {@link HelloWorld#set(BitSet, int) set(bitset, index)} at disjoint offsets are
         * compared.
         */
        @DisplayName("should compute <distance = |A| + |B|> when placements are disjoint")
        @Test
        void __disjoint() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 96);
            final var diff = (BitSet) a.clone();
            diff.xor(b);
            Assertions.assertEquals(a.cardinality() + b.cardinality(), diff.cardinality());
        }
    }

    /**
     * Demonstrates the Sieve of Eratosthenes — the textbook BitSet application. Each index
     * represents an integer; set bits mark composites; primes are the unset bits up to {@code N}.
     */
    @DisplayName("Sieve of Eratosthenes")
    @Nested
    class SieveOfEratosthenes_Test {

        /**
         * Verifies that the Sieve of Eratosthenes implemented over a {@link BitSet} counts
         * {@code 10} primes when sieving up to {@code 30}.
         */
        @DisplayName("should count <10> primes when sieving up to <30>")
        @Test
        void __primesUpTo30() {
            final int N = 30;
            final var composite = new BitSet(N + 1);
            composite.set(0);
            composite.set(1);
            for (int i = 2; (long) i * i <= N; i++) {
                if (!composite.get(i)) {
                    for (int j = i * i; j <= N; j += i) {
                        composite.set(j);
                    }
                }
            }
            final int primeCount = (N + 1) - composite.cardinality();
            Assertions.assertEquals(10, primeCount);
        }
    }
}
