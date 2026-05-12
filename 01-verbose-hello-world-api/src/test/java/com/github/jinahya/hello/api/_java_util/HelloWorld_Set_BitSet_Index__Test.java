package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.BitSet;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Set_BitSet_Index__Test
        extends HelloWorldTest {

    @BeforeEach
    void __a() {
        Mockito.doAnswer(i -> {
            final var bitset = i.getArgument(0, BitSet.class);
            var index = i.getArgument(1, Integer.class);
            for (int b : HelloWorldTestUtils.hello_world_byte_array()) {
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
    @Nested
    class Jaccard_Test {

        @DisplayName("identical placements → Jaccard = 1.0")
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

        @DisplayName("non-overlapping placements → Jaccard = 0.0")
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
    @Nested
    class Hamming_Test {

        @DisplayName("identical placements → distance = 0")
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

        @DisplayName("disjoint placements → distance = |A| + |B|")
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
    @Nested
    class SieveOfEratosthenes_Test {

        @DisplayName("primes ≤ 30 → 10 (2, 3, 5, 7, 11, 13, 17, 19, 23, 29)")
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
