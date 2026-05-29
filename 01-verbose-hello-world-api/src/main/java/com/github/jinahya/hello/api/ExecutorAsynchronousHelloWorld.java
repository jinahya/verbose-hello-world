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
 * The default executor-backed implementation of {@link AsynchronousHelloWorld}.
 * <p>
 * Each instance wraps a synchronous {@link HelloWorld} service of type {@code T} and an
 * {@link Executor} supplied at construction time. The two primitive overloads of
 * {@link #applyAsync(Function) applyAsync} dispatch every synchronous {@link HelloWorld} call onto
 * that executor — directly, with no intermediate thread of its own — and complete either the
 * returned {@link CompletionStage} ({@link #applyAsync(Function) stage form}) or the supplied
 * {@link CompletionHandler}
 * ({@link #applyAsync(Function, Object, CompletionHandler) handler form}).
 * <p>
 * The {@link CompletionHandler}-based primitive catches every {@link Throwable} thrown by the
 * mapper — including {@link Error} — and routes it through
 * {@link CompletionHandler#failed(Throwable, Object) handler.failed(...)}, guaranteeing that the
 * handler is always notified exactly once. The {@link CompletionStage}-based primitive delegates
 * to {@link CompletableFuture#supplyAsync(java.util.function.Supplier, Executor)} and so completes
 * its stage exceptionally on any {@link Throwable} from the mapper.
 * <p>
 * Instances are immutable and safe to share across threads; thread-safety of any I/O the wrapped
 * service performs is the wrapped service's responsibility.
 *
 * @param <T> the {@link HelloWorld} subtype wrapped by this instance.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld
 */
public final class ExecutorAsynchronousHelloWorld<T extends HelloWorld>
        implements AsynchronousHelloWorld<T> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance wrapping the specified service and dispatching its synchronous calls
     * onto the specified executor.
     *
     * @param service  the {@link HelloWorld} service that supplies the
     *                 <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>; must not
     *                 be {@code null}.
     * @param executor the executor onto which every synchronous {@link HelloWorld} call is
     *                 dispatched; must not be {@code null}.
     * @throws NullPointerException if either {@code service} or {@code executor} is {@code null}.
     */
    public ExecutorAsynchronousHelloWorld(final T service, final Executor executor) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation submits a task to the stored executor that, on completion of
     * {@code mapper.apply(service)}, invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(result, attachment)} —
     * or, on any {@link Throwable} thrown by the mapper (checked, unchecked, or {@link Error}),
     * invokes {@link CompletionHandler#failed(Throwable, Object) handler.failed(t, attachment)}.
     * The handler is guaranteed to be notified exactly once. A throw from
     * {@link CompletionHandler#completed(Object, Object) completed} or
     * {@link CompletionHandler#failed(Throwable, Object) failed} propagates to the executor's
     * {@link Thread.UncaughtExceptionHandler}.
     */
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

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation returns
     * {@link CompletableFuture#supplyAsync(java.util.function.Supplier, Executor)
     * CompletableFuture.supplyAsync(() -> mapper.apply(service), executor)}: the mapper runs on
     * the stored executor, and the returned stage completes with its result — or completes
     * exceptionally with any {@link Throwable} thrown by the mapper, wrapped in a
     * {@link CompletionException} per the {@link CompletableFuture} contract.
     */
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
