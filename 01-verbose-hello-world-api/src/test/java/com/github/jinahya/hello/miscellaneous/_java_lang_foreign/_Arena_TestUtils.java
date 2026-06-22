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

import java.lang.foreign.*;
import java.util.*;
import java.util.function.*;

/**
 * A class providing test utilities for {@link Arena} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({"java:S101"})
public final class _Arena_TestUtils {

    /**
     * Checks whether any of the libraries identified by the given {@code names} can be opened
     * through {@link SymbolLookup#libraryLookup(String, Arena)}.
     *
     * @param names candidate library names (or absolute paths), tried in order.
     * @return {@code true} if at least one library opens successfully; {@code false} otherwise.
     */
    public static boolean isLibraryAvailable(final String... names) {
        try (var arena = Arena.ofConfined()) {
            return findLibrary(arena, names).isPresent();
        }
    }

    /**
     * Returns the first available {@link SymbolLookup} for any of the libraries identified by the
     * given {@code names}, opened against the given {@code arena}.
     *
     * @param arena the arena that owns the returned lookup.
     * @param names candidate library names (or absolute paths), tried in order.
     * @return an {@link Optional} containing the first openable {@link SymbolLookup}, or empty if
     * none of the candidates could be opened.
     */
    public static Optional<SymbolLookup> findLibrary(final Arena arena, final String... names) {
        for (final var name : names) {
            try {
                return Optional.of(SymbolLookup.libraryLookup(name, arena));
            } catch (final Exception _) {
                // try next
            }
        }
        return Optional.empty();
    }

//    /**
//     * Reads {@code length} bytes from the start of the given {@code segment} and decodes them as a
//     * {@link StandardCharsets#US_ASCII US-ASCII} string.
//     *
//     * @param segment the segment to read from.
//     * @param length  the number of bytes to read.
//     * @return the decoded string.
//     */
//    public static String readSegmentAsString(final MemorySegment segment, final int length) {
//        final var bytes = new byte[length];
//        for (int i = 0; i < length; i++) {
//            bytes[i] = segment.get(ValueLayout.JAVA_BYTE, i);
//        }
//        return new String(bytes, StandardCharsets.US_ASCII);
//    }

    public static <R> R appyConfinedArena(final Function<? super Arena, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var arena = Arena.ofConfined()) {
            return function.apply(arena);
        }
    }

    public static void acceptConfinedArena(final Consumer<? super Arena> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        appyConfinedArena(a -> {
            consumer.accept(a);
            return null;
        });
    }

    public static <R> R appySharedArena(final Function<? super Arena, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var arena = Arena.ofShared()) {
            return function.apply(arena);
        }
    }

    public static void acceptSharedArena(final Consumer<? super Arena> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        appySharedArena(a -> {
            consumer.accept(a);
            return null;
        });
    }

    private _Arena_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
