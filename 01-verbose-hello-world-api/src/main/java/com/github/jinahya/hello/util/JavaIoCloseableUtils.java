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

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utilities for {@link java.io.Closeable} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class JavaIoCloseableUtils {

    public static void closeUnchecked(final Closeable closeable,
                                      final Consumer<? super IOException> consumer) {
        Objects.requireNonNull(closeable, "closeable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        JavaUtilConcurrentCallableUtils.callUnchecked(
                () -> {
                    closeable.close();
                    return closeable;
                },
                e -> {
                    if (e instanceof IOException ioe) {
                        consumer.accept(ioe);
                        return;
                    }
                    throw new RuntimeException(e);
                }
        );
    }

    public static <T extends Closeable> T closeUnchecked(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            closeable.close();
            return closeable;
        });
    }

    public static void closeSilently(final Closeable closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close {}", closeable, ioe);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoCloseableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
