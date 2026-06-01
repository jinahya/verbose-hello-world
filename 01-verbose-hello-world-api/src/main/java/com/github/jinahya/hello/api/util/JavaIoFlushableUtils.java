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

import java.io.*;
import java.util.*;

/**
 * Utilities for {@link java.io.Flushable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaIoFlushableUtils {

    /**
     * Invokes {@link Flushable#flush() flushable.flush()} and returns the {@code flushable},
     * wrapping any thrown {@link IOException} in a {@link RuntimeException} so callers can use this
     * in lambda contexts that disallow checked exceptions.
     *
     * @param flushable the {@link Flushable} to flush; must not be {@code null}.
     * @param <T>       the concrete {@link Flushable} subtype.
     * @return the given {@code flushable}, after flushing; never {@code null}.
     * @throws NullPointerException if {@code flushable} is {@code null}.
     * @throws RuntimeException     if {@link Flushable#flush() flush()} throws an
     *                              {@link IOException}; the cause is the original
     *                              {@link IOException}.
     * @see JavaUtilConcurrentCallableUtils#callUnchecked(java.util.concurrent.Callable)
     */
    public static <T extends Flushable> T flushUnchecked(final T flushable) {
        Objects.requireNonNull(flushable, "flushable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            flushable.flush();
            return flushable;
        });
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoFlushableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
