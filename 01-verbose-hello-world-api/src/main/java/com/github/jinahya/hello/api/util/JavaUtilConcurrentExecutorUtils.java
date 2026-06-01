package com.github.jinahya.hello.api.util;

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

import java.util.concurrent.*;

/**
 * Helpers for {@link Executor java.util.concurrent.Executor} — currently a single same-thread
 * factory useful for deterministic tests and synchronous fixtures.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaUtilConcurrentExecutorUtils {

    /**
     * Returns an {@link Executor} that runs every submitted command on the calling thread,
     * equivalent to {@code Runnable::run}. Useful for tests where async ordering must be
     * deterministic.
     *
     * @return a same-thread {@link Executor}; never {@code null}.
     * @see <a href="https://stackoverflow.com/q/6581188/330457">Is there an ExecutorService that
     * uses the current thread?</a>
     */
    public static Executor ofCurrentThread() {
        return Runnable::run;
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaUtilConcurrentExecutorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
