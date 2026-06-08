package com.github.jinahya.hello.miscellaneous;

import java.io.*;
import java.util.*;

public final class _Java_Util_BitSet_TestUtils {

    private _Java_Util_BitSet_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }

    /**
     * Prints the specified bit set's bits to the specified print stream, grouped by 64-bit long
     * words as returned by {@link BitSet#toLongArray()}.
     *
     * <pre>
     *        0                               31                              63
     * [  0]: 0 0 0 1 0 1 1 0 0 1 1 0 0 1 0 1 0 1 1 0 1 1 0 0 0 1 1 0 1 1 0 0 | 68 65 6C 6C 6F 2C 20 77
     * [  1]: 0 1 1 1 0 1 1 1 0 1 1 0 1 1 1 1 0 1 1 1 0 0 1 0 0 1 1 0 1 1 0 0 | 6F 72 6C 64 00 00 00 00
     * length: 96, cardinality: 48, size: 128
     * </pre>
     *
     * @param bitset  the bit set to print; must not be {@code null}.
     * @param printer the print stream to which the output is printed; must not be {@code null}.
     * @param <T>     the concrete {@link BitSet} subtype.
     * @return the given {@code bitset}, unchanged; never {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static <T extends BitSet> T print(final T bitset, final PrintStream printer) {
        Objects.requireNonNull(bitset, "bitset is null");
        Objects.requireNonNull(printer, "printer is null");
        final var longs = bitset.toLongArray();
        final var totalBits = longs.length * Long.SIZE;
        // ------------------------------------------------------------------------------------- header
        printer.print("       ");
        for (var i = 0; i < Long.SIZE; i++) {
            if (i == 0 || i == 31 || i == 63) {
                printer.printf("%-2d", i);
            } else {
                printer.print("  ");
            }
        }
        printer.println();
        // --------------------------------------------------------------------------------------- bits
        for (var w = 0; w < longs.length; w++) {
            printer.printf("[%3d]: ", w);
            for (var i = 0; i < Long.SIZE; i++) {
                printer.printf("%-2d", bitset.get(w * Long.SIZE + i) ? 1 : 0);
            }
            printer.print(" | ");
            for (var b = 0; b < Long.BYTES; b++) {
                var v = 0;
                for (var i = 0; i < Byte.SIZE; i++) {
                    if (bitset.get(w * Long.SIZE + b * Byte.SIZE + i)) {
                        v |= (1 << i);
                    }
                }
                printer.printf("%02X ", v);
            }
            printer.println();
        }
        // ------------------------------------------------------------------------------------- footer
        printer.printf("length: %d, cardinality: %d, size: %d%n",
                       bitset.length(), bitset.cardinality(), bitset.size());
        return bitset;
    }

    /**
     * The single-argument convenience of
     * {@link _Java_Util_BitSet_TestUtils#print(BitSet, PrintStream) print(bitset, System.out)}.
     *
     * @param bitset the bit set to print; must not be {@code null}.
     * @param <T>    the concrete {@link BitSet} subtype.
     * @return the given {@code bitset}, unchanged; never {@code null}.
     * @throws NullPointerException if {@code bitset} is {@code null}.
     */
    @SuppressWarnings({"java:S106"})
    public static <T extends BitSet> T print(final T bitset) {
        return print(bitset, System.out);
    }
}
