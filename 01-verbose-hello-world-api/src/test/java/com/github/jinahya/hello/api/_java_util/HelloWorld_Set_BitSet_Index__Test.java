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

import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#set(BitSet, int) set(bitset, index)} method with a real
 * {@link BitSet} and BitSet-based algorithms (Jaccard similarity, Dice coefficient, Hamming
 * distance, Sieve of Eratosthenes, Bloom filter, Bitap substring matching).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(bitset, index)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Set_BitSet_Index__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var bitset = i.getArgument(0, BitSet.class);
            var index = i.getArgument(1, Integer.class);
            for (int b : hello_world_byte_array()) {
                for (var j = 0; j < Byte.SIZE; j++) {
                    bitset.set(index++, (b & 1) == 1);
                    b >>>= 1;
                }
            }
            return bitset;
        }).when(service()).set(any(BitSet.class), anyInt());
    }

    // ---------------------------------------------------------------------------------------------

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
        assertEquals(95, bitset.length());
        assertEquals(48, bitset.cardinality());
        assertEquals(128, bitset.size());
    }

    /**
     * Demonstrates Jaccard similarity, {@code |A ∩ B| / |A ∪ B|}, computed with
     * {@link BitSet#and(BitSet)}, {@link BitSet#or(BitSet)}, and {@link BitSet#cardinality()}. Used
     * in deduplication, near-duplicate detection, and recommendation systems.
     */
    @DisplayName("Jaccard")
    @Nested
    class JaccardSimilarity_Test {

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
            final double jaccard = (double) intersection.cardinality() / union.cardinality();
            assertEquals(1.0, jaccard);
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
            final double jaccard = (double) intersection.cardinality() / union.cardinality();
            assertEquals(0.0, jaccard);
        }
    }

    /**
     * Demonstrates the Sørensen–Dice coefficient, {@code 2|A ∩ B| / (|A| + |B|)}, computed with
     * {@link BitSet#and(BitSet)} and {@link BitSet#cardinality()}. A companion to Jaccard with
     * heavier weight on the intersection; used in text similarity (bigram overlap, plagiarism
     * detection) and image segmentation evaluation.
     */
    @DisplayName("Dice")
    @Nested
    class DiceCoefficient_Test {

        /**
         * Verifies that the Dice coefficient equals {@code 1.0} when two {@link BitSet}s set by
         * {@link HelloWorld#set(BitSet, int) set(bitset, index)} have identical placements.
         */
        @DisplayName("should compute <Dice = 1.0> when placements are identical")
        @Test
        void __identical() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 0);
            final var intersection = (BitSet) a.clone();
            intersection.and(b);
            final double dice =
                    2.0 * intersection.cardinality() / (a.cardinality() + b.cardinality());
            assertEquals(1.0, dice);
        }

        /**
         * Verifies that the Dice coefficient equals {@code 0.0} when two {@link BitSet}s set by
         * {@link HelloWorld#set(BitSet, int) set(bitset, index)} at non-overlapping offsets are
         * compared.
         */
        @DisplayName("should compute <Dice = 0.0> when placements are non-overlapping")
        @Test
        void __disjoint() {
            final var a = new BitSet();
            final var b = new BitSet();
            service().set(a, 0);
            service().set(b, 96);
            final var intersection = (BitSet) a.clone();
            intersection.and(b);
            final double dice =
                    2.0 * intersection.cardinality() / (a.cardinality() + b.cardinality());
            assertEquals(0.0, dice);
        }
    }

    /**
     * Demonstrates Hamming distance, the count of differing bits, computed with
     * {@link BitSet#xor(BitSet)} and {@link BitSet#cardinality()}. Used in error-correcting codes
     * (Hamming, BCH), locality-sensitive hashing, and SimHash-style fingerprinting.
     */
    @DisplayName("Hamming")
    @Nested
    class HammingDistance_Test {

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
            assertEquals(0, diff.cardinality());
        }

        /**
         * Verifies that the Hamming distance equals {@code |A| + |B|} when two {@link BitSet}s set
         * by {@link HelloWorld#set(BitSet, int) set(bitset, index)} at disjoint offsets are
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
            assertEquals(a.cardinality() + b.cardinality(), diff.cardinality());
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
            assertEquals(10, primeCount);
        }
    }

    /**
     * Demonstrates a Bloom filter — a probabilistic membership structure. Each insert deposits a
     * known fingerprint via {@link HelloWorld#set(BitSet, int) set(bitset, index)} into the filter;
     * a query checks whether every bit of the queried fingerprint is present in the filter
     * ({@code (query AND filter) == query}). False positives are possible; false negatives are not.
     * Used in caches, URL shortlists, malicious-URL detection, and database join-key pruning.
     */
    @DisplayName("Bloom filter")
    @Nested
    class BloomFilter_Test {

        /**
         * Verifies that the membership test reports {@code mightContain = true} for an item whose
         * fingerprint was inserted via {@link HelloWorld#set(BitSet, int) set(bitset, index)}.
         */
        @DisplayName("should report <mightContain = true> for an inserted item")
        @Test
        void __present() {
            final var filter = new BitSet();
            service().set(filter, 0);
            final var query = new BitSet();
            service().set(query, 0);
            final var anded = (BitSet) query.clone();
            anded.and(filter);
            assertEquals(query, anded);
        }

        /**
         * Verifies that the membership test reports {@code mightContain = false} when at least one
         * bit of the queried fingerprint is missing from the filter.
         */
        @DisplayName("should report <mightContain = false> for an item not inserted")
        @Test
        void __absent() {
            final var filter = new BitSet();
            service().set(filter, 0);
            final var query = new BitSet();
            service().set(query, 200);
            final var anded = (BitSet) query.clone();
            anded.and(filter);
            assertNotEquals(query, anded);
        }
    }

    /**
     * Demonstrates Bitap (Shift-And) — bit-parallel exact substring matching. For a pattern of
     * length {@code m}, a per-character mask records the positions in the pattern that carry each
     * character; a state register {@code R} is updated for every text character with
     * {@code R = ((R << 1) | 1) AND mask[t]}, and a match ends at the current text position when
     * bit {@code m - 1} of {@code R} is set. Used in approximate string search ({@code agrep},
     * fuzzy file finders).
     */
    @DisplayName("Bitap (Shift-And) substring match")
    @Nested
    class BitapSubstringMatch_Test {

        /**
         * Verifies that the Bitap algorithm finds {@code "world"} in {@code "hello, world"} ending
         * at index {@code 11} (start index {@code 7}).
         */
        @DisplayName("should find <world> in <hello, world> ending at <11>")
        @Test
        void __found() {
            final var text = hello_world_byte_array();
            final byte[] pattern = {'w', 'o', 'r', 'l', 'd'};
            final var mask = new HashMap<Byte, BitSet>();
            for (var i = 0; i < pattern.length; i++) {
                mask.computeIfAbsent(pattern[i], k -> new BitSet()).set(i);
            }
            var register = new BitSet();
            var matchEnd = -1;
            for (var t = 0; t < text.length; t++) {
                final var shifted = new BitSet();
                for (var i = register.nextSetBit(0); i >= 0; i = register.nextSetBit(i + 1)) {
                    shifted.set(i + 1);
                }
                shifted.set(0);
                shifted.and(mask.getOrDefault(text[t], new BitSet()));
                register = shifted;
                if (register.get(pattern.length - 1)) {
                    matchEnd = t;
                    break;
                }
            }
            assertEquals(11, matchEnd);
            assertEquals(7, matchEnd - pattern.length + 1);
        }

        /**
         * Verifies that the Bitap algorithm reports no match when the pattern {@code "xyz"} is
         * searched in {@code "hello, world"}.
         */
        @DisplayName("should not find <xyz> in <hello, world>")
        @Test
        void __notFound() {
            final var text = hello_world_byte_array();
            final byte[] pattern = {'x', 'y', 'z'};
            final var mask = new HashMap<Byte, BitSet>();
            for (var i = 0; i < pattern.length; i++) {
                mask.computeIfAbsent(pattern[i], k -> new BitSet()).set(i);
            }
            var register = new BitSet();
            var found = false;
            for (final var t : text) {
                final var shifted = new BitSet();
                for (var i = register.nextSetBit(0); i >= 0; i = register.nextSetBit(i + 1)) {
                    shifted.set(i + 1);
                }
                shifted.set(0);
                shifted.and(mask.getOrDefault(t, new BitSet()));
                register = shifted;
                if (register.get(pattern.length - 1)) {
                    found = true;
                    break;
                }
            }
            assertFalse(found);
        }
    }
}
