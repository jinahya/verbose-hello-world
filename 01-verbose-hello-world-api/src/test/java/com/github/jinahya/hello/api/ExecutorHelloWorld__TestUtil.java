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

import java.util.function.*;

/**
 * A class providing test utilities for {@link ExecutorHelloWorld} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class ExecutorHelloWorld__TestUtil {

    /**
     * Returns a function that wraps the given {@link HelloWorld} instance in an
     * {@link ExecutorHelloWorld} backed by a {@link Runnable#run direct} executor.
     *
     * @param <T> the {@link HelloWorld} subtype.
     * @return an initializer function; never {@code null}.
     */
    public static <T extends HelloWorld>
    Function<? super T, ? extends ExecutorHelloWorld<T>> initializer() {
        return s -> new ExecutorHelloWorld<>(s, Runnable::run);
    }

    private ExecutorHelloWorld__TestUtil() {
        throw new AssertionError("instantiation is not allowed");
    }
}
