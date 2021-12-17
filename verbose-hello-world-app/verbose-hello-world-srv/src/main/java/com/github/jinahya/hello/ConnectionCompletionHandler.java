package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
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

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

class ConnectionCompletionHandler<A> implements CompletionHandler<Void, A> {

    ConnectionCompletionHandler(
            final BiConsumer<Void, ? super CompletionHandlerAttachment<Void, A>> completionConsumer,
            final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Void, A>> failureConsumer) {
        super();
        this.completionConsumer = requireNonNull(completionConsumer, "completionConsumer is null");
        this.failureConsumer = requireNonNull(failureConsumer, "failureConsumer is null");
    }

    @Override
    public void completed(final Void result, final A attachment) {
        completionConsumer.accept(result, CompletionHandlerAttachment.of(this, attachment));
    }

    @Override
    public void failed(final Throwable exc, final A attachment) {
        failureConsumer.accept(exc, CompletionHandlerAttachment.of(this, attachment));
    }

    private final BiConsumer<Void, ? super CompletionHandlerAttachment<Void, A>> completionConsumer;

    private final BiConsumer<Throwable, ? super CompletionHandlerAttachment<Void, A>> failureConsumer;
}
