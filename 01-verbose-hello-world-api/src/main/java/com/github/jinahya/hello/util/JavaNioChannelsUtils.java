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

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

/**
 * Utilities for {@link java.nio.channels} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaNioChannelsUtils {

    // @formatter:off
    public static <V, A> CompletionHandler<V, A> newCompletionHandler(
            final BiConsumer<? super V, ? super A> completed,
            final BiConsumer<? super Throwable, ? super A> failed) {
        return new CompletionHandler<V, A>() {
            @Override public void completed(final V result, final A attachment) {
                completed.accept(result, attachment);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                failed.accept(exc, attachment);
            }
        };
    }
    // @formatter:on

    private JavaNioChannelsUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
