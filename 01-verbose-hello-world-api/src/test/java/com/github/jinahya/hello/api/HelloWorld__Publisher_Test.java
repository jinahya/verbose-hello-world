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

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * An abstract base for subscription-level tests of the {@code HelloWorld<Type>Publisher}
 * {@link Flow.Publisher} implementations against the {@link java.util.concurrent.Flow} contract.
 * The constructor builds a {@link Mockito#mock(Class) mock} {@link HelloWorld} with
 * {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS}; {@link #applyPublisher(Function)} runs a
 * test action against a freshly created publisher and closes it if {@link AutoCloseable}.
 *
 * @param <U> the element type emitted by the publisher.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class HelloWorld__Publisher_Test<U> {

    HelloWorld__Publisher_Test(
            final Function<? super HelloWorld, ? extends Flow.Publisher<U>> initializer) {
        super();
        service = mock(HelloWorld.class, CALLS_REAL_METHODS);
        this.initializer = initializer;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the service so that {@code set(array)} writes the {@code hello-world-bytes} before each
     * test.
     */
    @BeforeEach
    void stubService() {
        set_array_sets_hello_world_bytes(service());
    }

    // ------------------------------------------------------------------------------------- service

    // --------------------------------------------------------------------------------- initializer

    /**
     * Creates a fresh publisher, runs the given function against it, and closes the publisher when
     * the function returns if it is {@link AutoCloseable}.
     *
     * @param function the function to apply to the publisher.
     * @param <R>      the return type of the given function.
     * @return the value returned by the given function.
     * @throws Exception if an error occurs while closing the publisher.
     */

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
