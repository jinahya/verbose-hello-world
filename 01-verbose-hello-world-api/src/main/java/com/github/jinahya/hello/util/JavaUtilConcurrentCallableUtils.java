package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class JavaUtilConcurrentCallableUtils {

    public static void callUnchecked(final Callable<?> callable,
                                     final Consumer<? super Exception> consumer) {
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try {
            callable.call();
        } catch (final Exception e) {
            consumer.accept(e);
        }
    }

    public static <V> V callUnchecked(final Callable<V> callable) {
        Objects.requireNonNull(callable, "callable is null");
        try {
            return callable.call();
        } catch (final Exception e) {
            if (e instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new RuntimeException("failed to call " + callable, e);
        }
    }

    private JavaUtilConcurrentCallableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
