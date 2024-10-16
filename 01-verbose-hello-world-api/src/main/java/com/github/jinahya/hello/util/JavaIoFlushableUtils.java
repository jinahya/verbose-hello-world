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

import java.io.Flushable;
import java.util.Objects;

/**
 * Utilities for {@link java.io.Flushable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaIoFlushableUtils {

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
