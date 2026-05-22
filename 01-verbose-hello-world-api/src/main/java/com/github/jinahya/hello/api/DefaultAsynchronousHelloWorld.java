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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * A default implementation of {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance wrapping the specified service and dispatching on the specified
     * executor.
     *
     * @param service  the {@link HelloWorld} service for the
     *                 <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @param executor the executor on which the synchronous {@link HelloWorld} calls are
     *                 dispatched.
     * @throws NullPointerException if either {@code service} or {@code executor} is {@code null}.
     */
    DefaultAsynchronousHelloWorld(final HelloWorld service, final Executor executor) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public <R> CompletionStage<R> applyAsync(
            final Function<? super HelloWorld, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return CompletableFuture.supplyAsync(
                () -> mapper.apply(service),
                executor
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final Executor executor;
}
