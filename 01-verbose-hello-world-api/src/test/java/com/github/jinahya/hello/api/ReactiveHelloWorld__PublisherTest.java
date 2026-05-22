package com.github.jinahya.hello.api;

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;

import java.util.function.Function;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingPublisher;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes;
import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.mock;

/**
 * An abstract base for tests that verify subscription-level behaviour of the three concrete
 * {@code ReactiveHelloWorld<Type>Publisher} implementations against the Reactive Streams 1.0
 * contract — demand, completion, the single-terminal-signal rule (1.7), cancellation, …
 * <p>
 * The constructor builds fresh per-test state (JUnit's default {@code PER_METHOD} lifecycle gives
 * every test a fresh instance, so the constructor runs once per test method):
 * <ul>
 *   <li>a {@link Mockito#mock(Class) mock} {@link HelloWorld} created with
 *       {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS} so the {@code default} methods on
 *       the interface stay live;</li>
 *   <li>the publisher produced by the constructor-supplied {@code initializer}, wrapped with
 *       {@link HelloWorldBookUtils#loggingPublisher(Publisher) loggingPublisher} so every
 *       {@code subscribe(...)} call is logged.</li>
 * </ul>
 * In addition, {@link #stubService()} is registered here as a {@code @BeforeEach} hook, stubbing
 * {@link HelloWorld#set(byte[]) service.set(...)} to write the actual {@code "hello, world"} bytes.
 * The hook is inherited by every concrete subclass, so subclasses do <em>not</em> need to redeclare
 * it.
 * <p>
 * Concrete subclasses are expected to be named {@code ReactiveHelloWorld_<Type>_PublisherTest}
 * (one per emitted element type — {@code Byte}, {@code Array}, {@code String}) and supply the
 * matching publisher factory to {@code super(...)} in their constructor.
 *
 * @param <U> the element type emitted by the publisher.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld_Byte_PublisherTest
 * @see ReactiveHelloWorld_Array_PublisherTest
 * @see ReactiveHelloWorld_String_PublisherTest
 */
@Slf4j
abstract class ReactiveHelloWorld__PublisherTest<U> {

    ReactiveHelloWorld__PublisherTest(
            final Function<? super HelloWorld, ? extends Publisher<U>> initializer) {
        super();
        requireNonNull(initializer, "initializer is null");
        service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        publisher = loggingPublisher(
                requireNonNull(initializer.apply(service), "null initialized")
        );
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        set_array_sets_actual_hello_world_bytes(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld service;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final Publisher<U> publisher;
}
