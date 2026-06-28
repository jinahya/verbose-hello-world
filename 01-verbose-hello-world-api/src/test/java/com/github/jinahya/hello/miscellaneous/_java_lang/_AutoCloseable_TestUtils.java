package com.github.jinahya.hello.miscellaneous._java_lang;

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

import java.lang.foreign.*;
import java.util.function.*;

/**
 * A class providing test utilities for {@link Arena} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({"java:S101"})
public final class _AutoCloseable_TestUtils {

    public static <T extends AutoCloseable, R> R appyConfinedArena(
            final Supplier<? extends T> supplier,
            final Function<? super T, ? extends R> function) throws Exception {
        try (var closeable = supplier.get()) {
            return function.apply(closeable);
        }
    }

    public static <T extends AutoCloseable> void acceptConfinedArena(
            final Supplier<? extends T> supplier,
            final Consumer<? super T> consumer) throws Exception {
        appyConfinedArena(
                supplier,
                c -> {
                    consumer.accept(c);
                    return null;
                }
        );
    }

    private _AutoCloseable_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
