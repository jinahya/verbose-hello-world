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

import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class JavaNioChannelsCompletionHandlerUtils {

    // @formatter:off
    public static <V, A> CompletionHandler<V, A> from(
            final BiConsumer<? super V, ? super A> completionConsumer,
            final BiConsumer<? super Throwable, ? super A> failureConsumer) {
        Objects.requireNonNull(completionConsumer, "completionConsumer is null");
        Objects.requireNonNull(failureConsumer, "failureConsumer is null");
        return new CompletionHandler<>() {
            @Override public void completed(final V result, final A attachment) {
                completionConsumer.accept(result, attachment);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                failureConsumer.accept(exc, attachment);
            }
        };
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaNioChannelsCompletionHandlerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
