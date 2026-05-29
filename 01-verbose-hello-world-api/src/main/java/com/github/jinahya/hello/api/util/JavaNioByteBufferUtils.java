package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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
 * Helpers for {@link ByteBuffer java.nio.ByteBuffer} — a textual visualizer that, in addition to
 * the buffer-shape strip emitted by {@link JavaNioBufferUtils}, also renders the underlying
 * {@code array} when {@link ByteBuffer#hasArray() hasArray()}, marking the {@code arrayOffset()}
 * window so a reader can see the buffer's view into its backing storage.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaNioByteBufferUtils {

    /**
     * Prints a multi-line, human-readable snapshot of {@code buffer}'s current state to
     * {@code printer}: identity hash, {@code remaining}, {@code direct}, {@code hasArray},
     * {@code arrayOffset} (when present), an ASCII strip marking {@code position}, {@code limit},
     * and {@code capacity}, and (when array-backed) a second strip overlaying the buffer's view on
     * its backing array.
     *
     * @param buffer  the byte buffer to render; must not be {@code null}.
     * @param printer the destination of the rendering; must not be {@code null}.
     * @param <T>     the concrete {@link ByteBuffer} subtype.
     * @return the given {@code buffer}, unchanged; never {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    @SuppressWarnings({
            "java:S1192"
    })
    public static <T extends ByteBuffer> T print(final T buffer, final PrintStream printer) {
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
        if (buffer.hasArray()) {
            for (int i = 0; i < (padding + buffer.arrayOffset() + 2); i++) {
                printer.print(' ');
            }
            printer.printf("%1$c arrayOffset(%2$d)%n", '↓', buffer.arrayOffset());
            printer.printf(String.format("%%1$%ds: ", padding), "array");
            for (int i = 0; i < buffer.arrayOffset(); i++) {
                printer.print('-');
            }
            var array = buffer.array();
            for (int i = buffer.arrayOffset(); i < buffer.arrayOffset() + buffer.capacity(); i++) {
                printer.print('+');
            }
            for (int i = buffer.arrayOffset() + buffer.capacity(); i < array.length; i++) {
                printer.print('-');
            }
            printer.printf(" %1$c length(%2$d) 0x%3$08x%n", '←', array.length,
                           System.identityHashCode(array));
        }
        printer.println("------------------------------------------------------------------------");
        return buffer;
    }

    /**
     * The single-argument convenience of
     * {@link #print(ByteBuffer, PrintStream) print(buffer, System.out)}.
     *
     * @param buffer the byte buffer to render; must not be {@code null}.
     * @param <T>    the concrete {@link ByteBuffer} subtype.
     * @return the given {@code buffer}, unchanged; never {@code null}.
     * @throws NullPointerException if {@code buffer} is {@code null}.
     */
    @SuppressWarnings({
            "java:S106"
    })
    public static <T extends ByteBuffer> T print(final T buffer) {
        return print(buffer, System.out);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaNioByteBufferUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
