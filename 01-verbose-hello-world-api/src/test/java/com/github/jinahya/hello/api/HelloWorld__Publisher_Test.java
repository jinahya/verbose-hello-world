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

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.mockito.Mockito.*;

/**
 * An abstract base for tests that verify subscription-level behaviour of the three concrete
 * {@code HelloWorld<Type>Publisher} {@link Flow.Publisher} implementations against the
 * {@link java.util.concurrent.Flow} contract.
 * <p>
 * The constructor builds fresh per-test state: a {@link Mockito#mock(Class) mock}
 * {@link HelloWorld} created with {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS}, and the
 * publisher produced by the {@code initializer} wrapped with
 * {@link HelloWorldBookUtils#loggingPublisher(Flow.Publisher) loggingPublisher} so every
 * {@code subscribe(...)} call is logged. The raw (un-proxied) {@code delegate} is also retained so
 * that {@link AutoCloseable} cleanup can run after each test.
 *
 * @param <U> the element type emitted by the publisher.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class HelloWorld__Publisher_Test<U> {

    HelloWorld__Publisher_Test(
            final Function<? super HelloWorld, ? extends Flow.Publisher<U>> initializer) {
        super();
        service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        this.initializer = initializer;
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        set_array_sets_hello_world_bytes(service());
    }

    // ------------------------------------------------------------------------------------- service

    // --------------------------------------------------------------------------------- initializer
    <R> R applyPublisher(final Function<? super Flow.Publisher<U>, ? extends R> function)
            throws Exception {
        final var publisher = initializer.apply(service);
        try {
            return function.apply(publisher);
        } finally {
            if (publisher instanceof AutoCloseable closeable) {
                closeable.close();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld service;

    private final Function<? super HelloWorld, ? extends Flow.Publisher<U>> initializer;
}
