package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

@Slf4j
class _Java_Util_Concurrent_ForkJoinPool_Test {

    /**
     * "Hello world" of work-stealing: pure deque mechanics, almost no real work per task.
     */
    @Nested
    class Fibonacci_Test {

        private static final class Fib
                extends RecursiveTask<Long> {

            private final int n;

            private Fib(final int n) {
                this.n = n;
            }

            @Override
            protected Long compute() {
                if (n <= 1) {
                    return (long) n;
                }
                final var f1 = new Fib(n - 1);
                f1.fork();
                final var f2 = new Fib(n - 2);
                return f2.compute() + f1.join();
            }
        }

        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var n = 25;
            final var expected = 75025L; // fib(25)
            // -------------------------------------------------------------------------------- when
            final var actual = ForkJoinPool.commonPool().invoke(new Fib(n));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(expected, actual);
        }
    }

    /**
     * Irregular tree-shaped parallelism: branches die early, others explode — work-stealing's
     * sweet spot.
     */
    @Nested
    class NQueens_Test {

        private static final class NQueens
                extends RecursiveTask<Long> {

            private final int n;

            private final int row;

            private final int[] cols;

            private NQueens(final int n, final int row, final int[] cols) {
                this.n = n;
                this.row = row;
                this.cols = cols;
            }

            @Override
            protected Long compute() {
                if (row == n) {
                    return 1L;
                }
                final var subtasks = new ArrayList<NQueens>(n);
                for (var c = 0; c < n; c++) {
                    if (!safe(c)) {
                        continue;
                    }
                    final var next = cols.clone();
                    next[row] = c;
                    final var sub = new NQueens(n, row + 1, next);
                    sub.fork();
                    subtasks.add(sub);
                }
                var count = 0L;
                for (final var s : subtasks) {
                    count += s.join();
                }
                return count;
            }

            private boolean safe(final int col) {
                for (var i = 0; i < row; i++) {
                    final var c = cols[i];
                    if (c == col || Math.abs(c - col) == row - i) {
                        return false;
                    }
                }
                return true;
            }
        }

        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var n = 8;
            final var expected = 92L; // 8-queens has 92 distinct solutions
            // -------------------------------------------------------------------------------- when
            final var actual = ForkJoinPool.commonPool().invoke(new NQueens(n, 0, new int[n]));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(expected, actual);
        }
    }

    /**
     * Balanced divide-and-conquer with real data movement: catches join/merge bugs that
     * "compute a number" tests miss.
     */
    @Nested
    class MergeSort_Test {

        private static final class MergeSort
                extends RecursiveAction {

            private static final int THRESHOLD = 1 << 13; // 8192

            private final int[] array;

            private final int lo;

            private final int hi;

            private MergeSort(final int[] array, final int lo, final int hi) {
                this.array = array;
                this.lo = lo;
                this.hi = hi;
            }

            @Override
            protected void compute() {
                if (hi - lo <= THRESHOLD) {
                    Arrays.sort(array, lo, hi);
                    return;
                }
                final var mid = (lo + hi) >>> 1;
                invokeAll(
                        new MergeSort(array, lo, mid),
                        new MergeSort(array, mid, hi)
                );
                merge(mid);
            }

            private void merge(final int mid) {
                final var left = Arrays.copyOfRange(array, lo, mid);
                final var right = Arrays.copyOfRange(array, mid, hi);
                var i = 0;
                var j = 0;
                var k = lo;
                while (i < left.length && j < right.length) {
                    array[k++] = left[i] <= right[j] ? left[i++] : right[j++];
                }
                while (i < left.length) {
                    array[k++] = left[i++];
                }
                while (j < right.length) {
                    array[k++] = right[j++];
                }
            }
        }

        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var random = new Random(0L);
            final var array = random.ints(100_000).toArray();
            final var expected = array.clone();
            Arrays.sort(expected);
            // -------------------------------------------------------------------------------- when
            ForkJoinPool.commonPool().invoke(new MergeSort(array, 0, array.length));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(expected, array);
        }
    }
}
