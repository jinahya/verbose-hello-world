package com.github.jinahya.hello.misc.c04chat;

/*-
 * #%L
 * verbose-hello-world-srv-common
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

import com.github.jinahya.hello.util.JavaIoCloseableUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
class ChatTcp5Server extends ChatTcp {

    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(
                newExecutorForServer("tcp-3-server-")
        );
        // ----------------------------------------------------------- read-quit!/shutdown-group-now
        JavaLangUtils.readLinesAndCallWhenTests(
                QUIT::equalsIgnoreCase,
                () -> {
                    group.shutdownNow();
                    return null;
                }
        );
        // ------------------------------------------------------------------------------------ open
        try (var server = AsynchronousServerSocketChannel.open(group);
             var publisher = new SubmissionPublisher<_ChatMessage.OfBuffer>()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, SERVER_BACKLOG);
            log.debug("bound");
            // ------------------------------------------------------------------------------ accept
            server.<Void>accept(null, new CompletionHandler<>() {
                @Override
                public void completed(final AsynchronousSocketChannel client,
                                      final Void attachment) {
                    new _ChatMessage.OfBuffer().readyToReadFromClient().read(
                            client,
                            new CompletionHandler<>() {
                                @Override
                                public void completed(final Integer w,
                                                      final _ChatMessage.OfBuffer message) {
                                    if (!message.hasRemaining()) {
                                        publisher.submit(_ChatMessage.OfBuffer.copyOf(message));
                                        message.print().readyToReadFromClient();
                                    }
                                    message.read(client, this);
                                }

                                @Override
                                public void failed(final Throwable exc,
                                                   final _ChatMessage.OfBuffer message) {
                                    JavaIoCloseableUtils.closeSilently(client);
                                }
                            }
                    );
                    final var messages = new LinkedBlockingQueue<_ChatMessage.OfBuffer>(8);
                    final var subscriptionRef = new AtomicReference<Flow.Subscription>();
                    publisher.subscribe(new Flow.Subscriber<>() {
                        @Override
                        public void onSubscribe(final Flow.Subscription subscription) {
                            subscriptionRef.set(subscription);
                            subscriptionRef.get().request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(final _ChatMessage.OfBuffer item) {
                            if (!messages.offer(_ChatMessage.OfBuffer.copyOf(item))) {
                                log.error("failed to offer");
                            }
                        }

                        @Override
                        public void onError(final Throwable throwable) {
                            log.error("error", throwable);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
                    // -------------------------------------------------------------- keep-accepting
                    server.accept(null, this);
                }

                @Override
                public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to accept", exc);
                }
            });
            // ------------------------------------------------ keep-awaiting-group-to-be-terminated
            while (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                // empty
            }
        }
    }

    private ChatTcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
