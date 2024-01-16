package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An interface for invoking methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface HelloWorldExecutor {

    // ---------------------------------------------------------------------------------------------
    default <T> Future<T> executeAsync(
            final Supplier<? extends HelloWorld> supplier,
            final Function<? super HelloWorld, ? extends T> function,
            final Executor executor) {
        Objects.requireNonNull(supplier, "supplier is null");
        Objects.requireNonNull(function, "function is null");
        Objects.requireNonNull(executor, "executor is null");
        final var command = new FutureTask<T>(() -> function.apply(supplier.get()));
        executor.execute(command); // Runnable  <- RunnableFuture<V> <- FutureTask<V>
        return command;            // Future<V> <- RunnableFuture<V> <- FutureTask<V>
    }

    default <T> CompletableFuture<T> completeAsync(
            final Supplier<? extends HelloWorld> supplier,
            final Function<? super HelloWorld, ? extends T> function,
            final Executor executor) {
        Objects.requireNonNull(supplier, "supplier is null");
        Objects.requireNonNull(function, "function is null");
        Objects.requireNonNull(executor, "executor is null");
        return CompletableFuture.supplyAsync(
                () -> function.apply(supplier.get()),
                executor
        );
    }
}
