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

import org.jspecify.annotations.*;

import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * A default implementation of {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class AsynchronousHelloWorldImpl<T extends HelloWorld> implements AsynchronousHelloWorld<T> {

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
    AsynchronousHelloWorldImpl(final T service, final Executor executor) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public <R, A> void applyAsync(final Function<? super T, ? extends R> mapper,
                                  final @Nullable A attachment,
                                  final CompletionHandler<? super R, ? super A> handler) {
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(handler, "handler is null");
        executor.execute(() -> {
            final R result;
            try {
                result = mapper.apply(service);
            } catch (final Throwable t) {
                handler.failed(t, attachment);
                return;
            }
            handler.completed(result, attachment);
        });
    }

    @Override
    public <R> CompletionStage<R> applyAsync(final Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return CompletableFuture.supplyAsync(
                () -> mapper.apply(service),
                executor
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final T service;

    private final Executor executor;
}
