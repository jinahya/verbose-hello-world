package com.github.jinahya.hello.miscellaneous;

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

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * A class providing test utilities for {@link Buffer java.nio.Buffer} &mdash; currently a textual
 * visualizer that prints a buffer's {@code position} / {@code limit} / {@code capacity} state as a
 * labeled ASCII strip.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class _Java_Nio_Buffer_TestUtils {

    private _Java_Nio_Buffer_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }

    /**
     * Prints a multi-line, human-readable snapshot of {@code buffer}'s current state to
     * {@code printer}: identity hash, {@code remaining}, {@code direct}, {@code hasArray},
     * {@code arrayOffset} (when present), and an ASCII strip marking {@code position},
     * {@code limit}, and {@code capacity}.
     *
     * @param buffer  the buffer to render; must not be {@code null}.
     * @param printer the destination of the rendering; must not be {@code null}.
     * @param <T>     the concrete {@link Buffer} subtype of {@code buffer}.
     * @return the given {@code buffer}, unchanged; never {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    @SuppressWarnings({
            "java:S1192"
    })
    public static <T extends Buffer> T print(final T buffer, final PrintStream printer) {
        Objects.requireNonNull(buffer, "buffer is null");
        var padding = 11;
        printer.println("------------------------------------------------------------------------");
        printer.printf(String.format("%%1$%ds: %%2$s 0x%%3$08x%%n", padding), "buffer", buffer,
                       System.identityHashCode(buffer));

        printer.printf(String.format("%%1$%ds: %%2$d%%n", padding), "remaining",
                       buffer.remaining());

        printer.printf(String.format("%%1$%ds: %%2$b%%n", padding), "direct", buffer.isDirect());
        printer.printf(String.format("%%1$%ds: %%2$b%%n", padding), "hasArray", buffer.hasArray());
        if (buffer.hasArray()) {
            printer.printf(String.format("%%1$%ds: %%2$d%%n", padding), "arrayOffset",
                           buffer.arrayOffset());
        }
        printer.println("------------------------------------------------------------------------");
        var arrayOffset = buffer.hasArray() ? buffer.arrayOffset() : 0;
        var ppadding = padding + arrayOffset + buffer.position() + 3;
        printer.printf(String.format("%%1$%dc pos(%%2$d)%%n", ppadding), '↓', buffer.position());
        printer.printf(String.format("%%1$%ds: ", padding), "buffer");
        for (int i = 0; i < arrayOffset; i++) {
            printer.print(' ');
        }
        for (int i = 0; i < buffer.position(); i++) {
            printer.print('-');
        }
        for (int i = 0; i < buffer.remaining(); i++) {
            printer.print('*');
        }
        for (int i = buffer.position() + buffer.remaining(); i < buffer.capacity(); i++) {
            printer.print('-');
        }
        printer.printf(" %1$c cap(%2$d)%n", '←', buffer.capacity());
        var lpadding = padding + arrayOffset + buffer.limit() + 3;
        printer.printf(String.format("%%1$%dc lim(%%2$d)%%n", lpadding), '↑', buffer.limit());
        printer.println("------------------------------------------------------------------------");
        return buffer;
    }

    /**
     * The single-argument convenience of
     * {@link _Java_Nio_Buffer_TestUtils#print(Buffer, PrintStream) print(buffer, System.out)}.
     *
     * @param buffer the buffer to render; must not be {@code null}.
     * @param <T>    the concrete {@link Buffer} subtype of {@code buffer}.
     * @return the given {@code buffer}, unchanged; never {@code null}.
     * @throws NullPointerException if {@code buffer} is {@code null}.
     */
    @SuppressWarnings({
            "java:S106"
    })
    public static <T extends Buffer> T print(final T buffer) {
        return _Java_Nio_Buffer_TestUtils.print(buffer, System.out);
    }
}
