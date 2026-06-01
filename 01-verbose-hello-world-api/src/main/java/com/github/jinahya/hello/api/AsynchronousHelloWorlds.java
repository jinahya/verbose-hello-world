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

import java.util.concurrent.*;

/**
 * Factory and utility methods for {@link AsynchronousHelloWorld} implementations.
 * <p>
 * Mirrors the spirit of {@link java.util.concurrent.Executors}: a non-instantiable holder of static
 * factory methods that hide the concrete strategy from the caller.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld
 */
final class AsynchronousHelloWorlds {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new {@link AsynchronousHelloWorld} backed by the specified {@link Executor},
     * dispatching every synchronous {@link HelloWorld} call onto it.
     *
     * @param <T>      the {@link HelloWorld} subtype to wrap.
     * @param service  the {@link HelloWorld} service that supplies the
     *                 <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>; must not
     *                 be {@code null}.
     * @param executor the executor onto which every synchronous {@link HelloWorld} call is
     *                 dispatched; must not be {@code null}.
     * @return a new {@link AsynchronousHelloWorld} backed by {@code executor}.
     * @throws NullPointerException if either {@code service} or {@code executor} is {@code null}.
     * @see ExecutorHelloWorld
     */
    static <T extends HelloWorld> AsynchronousHelloWorld<T>
    executorInstance(final T service, final Executor executor) {
        return new ExecutorHelloWorld<>(service, executor);
    }

    /**
     * Creates a new {@link AsynchronousHelloWorld} that opens a per-invocation
     * {@link StructuredTaskScope} for each call.
     *
     * @param <T>     the {@link HelloWorld} subtype to wrap.
     * @param service the {@link HelloWorld} service that supplies the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>; must not
     *                be {@code null}.
     * @return a new {@link AsynchronousHelloWorld} that runs each call inside a structured task
     * scope on a virtual thread.
     * @throws NullPointerException if {@code service} is {@code null}.
     * @see StructuredConcurrencyHelloWorld
     */
    static <T extends HelloWorld> AsynchronousHelloWorld<T>
    structuredConcurrencyInstance(final T service) {
        return new StructuredConcurrencyHelloWorld<>(service,
                                                     java.util.function.UnaryOperator.identity());
    }

    // ---------------------------------------------------------------------------------------------
    private AsynchronousHelloWorlds() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
