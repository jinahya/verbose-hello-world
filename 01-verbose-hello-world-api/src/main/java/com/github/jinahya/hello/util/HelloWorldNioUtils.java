package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import java.nio.Buffer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilities for {@link java.nio} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class HelloWorldNioUtils {

    public static <B extends Buffer, R> R flipApplyAndRestore(
            B buffer, Function<? super B, ? extends R> function) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(function, "function is null");
        var l = buffer.limit();
        var p = buffer.position();
        buffer.flip(); // limit -> position, position -> zero
        try {
            return function.apply(buffer);
        } finally {
            buffer.limit(l).position(p);
        }
    }

    public static <B extends Buffer> void flipAcceptAndRestore(B buffer,
                                                               Consumer<? super B> consumer) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(consumer, "consumer is null");
        flipApplyAndRestore(
                buffer,
                b -> {
                    consumer.accept(b);
                    return null;
                }
        );
    }

    private HelloWorldNioUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
