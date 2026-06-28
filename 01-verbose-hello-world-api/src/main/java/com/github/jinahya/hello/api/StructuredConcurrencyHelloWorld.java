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
 * A {@link StructuredTaskScope}-backed implementation of {@link AsynchronousHelloWorld}.
 * <p>
 * Each instance wraps a synchronous {@link HelloWorld} service of type {@code T} supplied at
 * construction time. Unlike {@link ExecutorHelloWorld}, this implementation does not take an
 * {@link Executor}; each {@code applyAsync(...)} call spawns a virtual thread that opens a
 * per-invocation {@link StructuredTaskScope}, forks the mapper as a single subtask, joins the
 * scope, and signals completion through either the supplied {@link CompletionHandler}
 * ({@link #applyAsync(Function, Object, CompletionHandler) handler form}) or the returned
 * {@link CompletionStage} ({@link #applyAsync(Function) stage form}).
 * <p>
 * The scope's lifecycle is bound to one invocation — its try-with-resources block — so the
 * structured-concurrency cancellation semantics (close the scope → cancel the in-flight subtask)
 * apply per call rather than per service instance.
 * <p>
 * Instances are immutable and safe to share across threads; thread-safety of any I/O the wrapped
 * service performs is the wrapped service's responsibility.
 *
 * @param <T> the {@link HelloWorld} subtype wrapped by this instance.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld
 * @see ExecutorHelloWorld
 */
public final class StructuredConcurrencyHelloWorld<T extends HelloWorld>
        implements AsynchronousHelloWorld<T> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance wrapping the specified service, opening each per-invocation
     * {@link StructuredTaskScope} with a {@link StructuredTaskScope.Configuration} customized by
     * the specified configurator.
     *
     * @param service      the {@link HelloWorld} service that supplies the
     *                     <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>; must
     *                     not be {@code null}.
     * @param configurator a function that customizes the {@link StructuredTaskScope.Configuration}
     *                     applied to every per-invocation scope (timeout, {@code ThreadFactory},
     *                     name, etc.); must not be {@code null}. Use
     *                     {@link UnaryOperator#identity() UnaryOperator.identity()} for the default
     *                     configuration.
     * @throws NullPointerException if either {@code service} or {@code configurator} is
     *                              {@code null}.
     */
    public StructuredConcurrencyHelloWorld(
            final T service,
            final UnaryOperator<StructuredTaskScope.Configuration> configurator) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.configurator = Objects.requireNonNull(configurator, "configurator is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation spawns a virtual thread that opens a
     * {@link StructuredTaskScope}, forks {@code () -> mapper.apply(service)} as a single subtask,
     * and joins the scope. On {@link StructuredTaskScope.Subtask.State#SUCCESS SUCCESS} it invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(result, attachment)}; on
     * {@link StructuredTaskScope.Subtask.State#FAILED FAILED} or any {@link Throwable} thrown while
     * opening or joining the scope (including {@link InterruptedException}), it invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(t, attachment)}. The
     * handler is guaranteed to be notified exactly once.
     */
    @Override
    public <R, A> void applyAsync(final Function<? super T, ? extends R> mapper,
                                  final @Nullable A attachment,
                                  final CompletionHandler<? super R, ? super A> handler) {
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(handler, "handler is null");
        Thread.startVirtualThread(() -> {
            try (var scope = StructuredTaskScope.open(
                    StructuredTaskScope.Joiner.<R>awaitAll(),
                    configurator
            )) {
                final var task = scope.fork(() -> mapper.apply(service));
                scope.join();
                if (task.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    handler.completed(task.get(), attachment);
                } else {
                    handler.failed(task.exception(), attachment);
                }
            } catch (final Throwable t) {
                handler.failed(t, attachment);
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation spawns a virtual thread that opens a
     * {@link StructuredTaskScope}, forks {@code () -> mapper.apply(service)} as a single subtask,
     * and joins the scope. On {@link StructuredTaskScope.Subtask.State#SUCCESS SUCCESS} it
     * completes the returned stage with the subtask's result; on
     * {@link StructuredTaskScope.Subtask.State#FAILED FAILED} or any {@link Throwable} thrown while
     * opening or joining the scope (including {@link InterruptedException}), it completes the
     * returned stage exceptionally with that cause.
     */
    @Override
    public <R> CompletionStage<R> applyAsync(final Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        final var future = new CompletableFuture<R>();
        Thread.startVirtualThread(() -> {
            try (var scope = StructuredTaskScope.open(
                    StructuredTaskScope.Joiner.<R>awaitAll(),
                    configurator
            )) {
                final var task = scope.fork(() -> mapper.apply(service));
                scope.join();
                if (task.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    future.complete(task.get());
                } else {
                    future.completeExceptionally(task.exception());
                }
            } catch (final Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    // ---------------------------------------------------------------------------------------------
    private final T service;

    private final UnaryOperator<StructuredTaskScope.Configuration> configurator;
}
