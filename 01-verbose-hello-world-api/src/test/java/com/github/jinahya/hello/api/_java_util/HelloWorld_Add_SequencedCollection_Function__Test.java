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
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link HelloWorld#add(SequencedCollection, Function) add(collection, mapper)} method with real
 * {@link SequencedCollection} subtypes from {@code java.util} and {@code java.util.concurrent}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("add(collection, mapper)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Add_SequencedCollection_Function__Test extends HelloWorld__Test {

    private static void print(final boolean unsupported,
                              final SequencedCollection<String> collection) {
        if (unsupported) {
            System.out.println(
                    String.format("%22s: UNSUPPORTED", collection.getClass().getSimpleName())
            );
            return;
        }
        System.out.println(
                collection.stream()
                        .collect(Collectors.joining(
                                " ",
                                String.format("%22s: [", collection.getClass().getSimpleName()),
                                "]"
                        ))
        );
    }

    private static void print(final SequencedCollection<String> collection) {
        print(false, collection);
    }

    private static void printStep(final String op, final String element,
                                  final SequencedCollection<String> collection) {
        System.out.printf("%s(%s)   → [%s]%n", op, element, String.join(" ", collection));
    }

    private static Function<Byte, String> DECODER = b -> String.valueOf((char) b.byteValue());

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() {
        // AI: should be equivalent to the HelloWorld#add(collection, mapper)
        doAnswer(i -> {
            final var collection = i.getArgument(0, SequencedCollection.class);
            final Function<? super Byte, ?> function = i.getArgument(1, Function.class);
            hello_world_byte_stream().map(function).forEach(collection::addLast);
            return collection;
        }).when(service()).add(any(), any());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("addLast")
    @Nested
    class AddLast_Test {

        @DisplayName("list")
        @Nested
        class List_Test {

            @DisplayName("should add through <ArrayList>")
            @Test
            void __ArrayList() {
                final var collection = new ArrayList<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            @DisplayName("should add through <LinkedList>")
            @Test
            void __LinkedList() {
                final var collection = new LinkedList<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            @DisplayName("should add through <Vector>")
            @Test
            void __Vector() {
                final var collection = new Vector<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            @DisplayName("should add through <Stack>")
            @Test
            void __Stack() {
                final var collection = new Stack<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            @DisplayName("should add through <CopyOnWriteArrayList>")
            @Test
            void __CopyOnWriteArrayList() {
                final var collection = new CopyOnWriteArrayList<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }
        }

        // ---------------------------------------------------------------------------------------------
        @DisplayName("deque")
        @Nested
        class Deque_Test {

            @DisplayName("should add through <ArrayDeque>")
            @Test
            void __ArrayDeque() {
                final var collection = new ArrayDeque<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            // duplicate — covered by List_Test.__LinkedList
//        @Test
//        void __LinkedList() {
//            final Deque<String> collection = new LinkedList<>();
//            service().add(collection, DECODER);
//            print(collection);
//            assertArrayEquals(hello_world_int_array(),
//                              collection.stream().mapToInt(Integer::intValue).toArray());
//        }

            @DisplayName("should add through <LinkedBlockingDeque>")
            @Test
            void __LinkedBlockingDeque() {
                final var collection = new LinkedBlockingDeque<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }

            @DisplayName("should add through <ConcurrentLinkedDeque>")
            @Test
            void __ConcurrentLinkedDeque() {
                final var collection = new ConcurrentLinkedDeque<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }
        }

        @DisplayName("sequenced set")
        @Nested
        class SequencedSet_Test {

            @DisplayName("should add through <LinkedHashSet>")
            @Test
            void __LinkedHashSet() {
                final var collection = new LinkedHashSet<String>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
                final var expected = hello_world_byte_stream().map(DECODER)
                        .collect(Collectors.toSet());
                assertEquals(expected, collection);
            }
        }

        // -----------------------------------------------------------------------------------------
        @DisplayName("sorted set")
        @Nested
        class SortedSet_Test {

            @DisplayName("should add through <TreeSet>")
            @Test
            void __TreeSet() {
                final SortedSet<String> collection = new TreeSet<>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
            }
        }

        @DisplayName("navigable set")
        @Nested
        class NavigableSet_Test {

            @DisplayName("should add through <ConcurrentSkipListSet>")
            @Test
            void __ConcurrentSkipListSet() {
                final NavigableSet<String> collection = new ConcurrentSkipListSet<>();
                try {
                    service().add(collection, DECODER);
                } catch (final UnsupportedOperationException uoe) {
                    print(true, collection);
                    return;
                }
                print(collection);
                final var expected = hello_world_byte_stream().map(DECODER)
                        .collect(Collectors.toSet());
                assertEquals(expected, collection);
            }
        }
    }

    @DisplayName("add")
    @Nested
    class Add_Test {

        @DisplayName("LinkedHashSet")
        @Nested
        class LinkedHashSet_Test {

            @DisplayName("should add through <LinkedHashSet#add>")
            @Test
            void __() {
                final var collection = new LinkedHashSet<String>();
                hello_world_byte_stream().map(DECODER).forEach(collection::add);
                print(collection);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("LinkedHashSet trace")
    @Nested
    class LinkedHashSet_Trace_Test {

        @DisplayName("should trace <addLast> over <LinkedHashSet>")
        @Test
        void __addLast() {
            final var collection = new LinkedHashSet<String>();
            for (final var b : hello_world_byte_array()) {
                final var element = DECODER.apply(b);
                collection.addLast(element);
                printStep("addLast", element, collection);
            }
        }

        @DisplayName("should trace <add> over <LinkedHashSet>")
        @Test
        void __add() {
            final var collection = new LinkedHashSet<String>();
            for (final var b : hello_world_byte_array()) {
                final var element = DECODER.apply(b);
                collection.add(element);
                printStep("add", element, collection);
            }
        }
    }
}
