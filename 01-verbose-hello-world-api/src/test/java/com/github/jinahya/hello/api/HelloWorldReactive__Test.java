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

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * An abstract base for tests demonstrating a reactive library's own publisher-creation idioms
 * against {@link HelloWorld} / {@link AsynchronousHelloWorld} — the library defines its own
 * publisher and fetches the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
 * directly from the service (no intermediate {@code org.reactivestreams.Publisher}). Subclasses
 * are named {@code HelloWorldReactive_<Library>_Test}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class HelloWorldReactive__Test {

    HelloWorldReactive__Test() {
        super();
        this.synchronousService = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        this.asynchronousService = spy(new ExecutorHelloWorld<>(synchronousService, Runnable::run));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the synchronous service so that {@code set(array)} writes the {@code hello-world-bytes}
     * before each test.
     */
    @BeforeEach
    void stubService() {
        set_array_sets_hello_world_bytes(synchronousService);
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld synchronousService;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final AsynchronousHelloWorld<HelloWorld> asynchronousService;
}
