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
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link HelloWorld#put(SequencedMap, Function, Function) put(map, keyMapper, valueMapper)} method
 * with real {@link SequencedMap} subtypes from {@code java.util} and {@code java.util.concurrent}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("put(map, keyMapper, valueMapper)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Put_SequencedMap_Function_Function__Test extends HelloWorld__Test {

    private static void print(final boolean unsupported,
                              final SequencedMap<String, String> map) {
        if (unsupported) {
            System.out.println(
                    String.format("%22s: UNSUPPORTED", map.getClass().getSimpleName())
            );
            return;
        }
        System.out.println(
                map.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(
                                " ",
                                String.format("%22s: [", map.getClass().getSimpleName()),
                                "]"
                        ))
        );
    }

    private static void print(final SequencedMap<String, String> map) {
        print(false, map);
    }

    private static void printStep(final String op, final String key, final String value,
                                  final SequencedMap<String, String> map) {
        final var contents = map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" "));
        System.out.printf("%s(%s,%s)   → [%s]%n", op, key, value, contents);
    }

    private static Function<Byte, String> DECODER = b -> String.valueOf((char) b.byteValue());

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() {
        // AI: should be equivalent to the HelloWorld#put(map, keyMapper, valueMapper)
        doAnswer(i -> {
            final var map = i.getArgument(0, SequencedMap.class);
            final Function<? super Byte, ?> keyFn = i.getArgument(1, Function.class);
            final Function<? super Byte, ?> valFn = i.getArgument(2, Function.class);
            hello_world_byte_stream().forEach(b -> map.putLast(keyFn.apply(b), valFn.apply(b)));
            return map;
        }).when(service()).put(any(), any(), any());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("putLast")
    @Nested
    class PutLast_Test {

        @DisplayName("sequenced map")
        @Nested
        class SequencedMap_Test {

            @DisplayName("LinkedHashMap")
            @Test
            void __LinkedHashMap() {
                // --------------------------------------------------------------------------- given
                final var map = new LinkedHashMap<String, String>();
                // ---------------------------------------------------------------------------- when
                try {
                    service().put(map, DECODER, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, map);
                    return;
                }
                // ---------------------------------------------------------------------------- then
                print(map);
                final var expected = hello_world_byte_stream().map(DECODER)
                        .collect(Collectors.toSet());
                assertEquals(expected, map.keySet());
            }
        }

        // -----------------------------------------------------------------------------------------
        @DisplayName("sorted map")
        @Nested
        class SortedMap_Test {

            @DisplayName("TreeMap")
            @Test
            void __TreeMap() {
                // --------------------------------------------------------------------------- given
                final SortedMap<String, String> map = new TreeMap<>();
                // ---------------------------------------------------------------------------- when
                try {
                    service().put(map, DECODER, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, map);
                    return;
                }
                // ---------------------------------------------------------------------------- then
                print(map);
                final var expected = hello_world_byte_stream().map(DECODER)
                        .collect(Collectors.toSet());
                assertEquals(expected, map.keySet());
            }
        }

        // -----------------------------------------------------------------------------------------
        @DisplayName("navigable map")
        @Nested
        class NavigableMap_Test {

            // duplicate — covered by SortedMap_Test.__TreeMap
//        @Test
//        void __TreeMap() {
//            // ----------------------------------------------------------------------------- given
//            final NavigableMap<String, String> map = new TreeMap<>();
//            // ------------------------------------------------------------------------------ when
//            try {
//                service().put(map, DECODER, DECODER);
//            } catch (final UnsupportedOperationException uoe) {
//                print(true, map);
//                return;
//            }
//            // ------------------------------------------------------------------------------ then
//            print(map);
//            final var expected = hello_world_byte_stream().map(DECODER)
//                    .collect(Collectors.toSet());
//            assertEquals(expected, map.keySet());
//        }

            @DisplayName("ConcurrentSkipListMap")
            @Test
            void __ConcurrentSkipListMap() {
                // --------------------------------------------------------------------------- given
                final NavigableMap<String, String> map = new ConcurrentSkipListMap<>();
                // ---------------------------------------------------------------------------- when
                try {
                    service().put(map, DECODER, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, map);
                    return;
                }
                // ---------------------------------------------------------------------------- then
                print(map);
                final var expected = hello_world_byte_stream().map(DECODER)
                        .collect(Collectors.toSet());
                assertEquals(expected, map.keySet());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("put")
    @Nested
    class Put_Test {

        @DisplayName("LinkedHashMap")
        @Nested
        class LinkedHashMap_Test {

            @DisplayName("happy path")
            @Test
            void __() {
                // --------------------------------------------------------------------------- given
                final var map = new LinkedHashMap<String, String>();
                // ---------------------------------------------------------------------------- when
                hello_world_byte_stream().map(DECODER).forEach(s -> map.put(s, s));
                // ---------------------------------------------------------------------------- then
                print(map);
                // Map.put(k, v) on LinkedHashMap is a value-update when k is present; it does NOT
                // move the entry. Encounter order is first-occurrence: h, e, l, o, ',', ' ', w, r, d
                assertIterableEquals(
                        List.of("h", "e", "l", "o", ",", " ", "w", "r", "d"),
                        map.keySet()
                );
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("LinkedHashMap trace")
    @Nested
    class LinkedHashMap_Trace_Test {

        @DisplayName("putLast")
        @Test
        void __putLast() {
            final var map = new LinkedHashMap<String, String>();
            for (final var b : hello_world_byte_array()) {
                final var s = DECODER.apply(b);
                map.putLast(s, s);
                printStep("putLast", s, s, map);
            }
        }

        @DisplayName("put")
        @Test
        void __put() {
            final var map = new LinkedHashMap<String, String>();
            for (final var b : hello_world_byte_array()) {
                final var s = DECODER.apply(b);
                map.put(s, s);
                printStep("put", s, s, map);
            }
        }
    }
}
