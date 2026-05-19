package com.github.jinahya.hello.lib;

/*-
 * #%L
 * verbose-hello-world-lib
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

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

final class HelloWorldUtils {

    static <R> R applyHelloWorldBytes(final Function<? super byte[], ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(
                HelloWorldConstants.HELLO_WORLD_STRING.getBytes(StandardCharsets.US_ASCII)
        );
    }

    static void acceptHelloWorldBytes(final Consumer<? super byte[]> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyHelloWorldBytes(b -> {
            consumer.accept(b);
            return null;
        });
    }

    private HelloWorldUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
