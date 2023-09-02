package com.github.jinahya.hello.miscellaneous.c03chat;

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

import com.github.jinahya.hello.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp3Server {

    // @formatter:off
    static class ChatTcp3ServerAttachment extends ChatTcp2Server.ChatTcp2ServerAttachment
            implements Flow.Subscriber<ByteBuffer>, Closeable {
        ChatTcp3ServerAttachment(AsynchronousSocketChannel client,
                                 SubmissionPublisher<ByteBuffer> publisher) {
            super();
            this.client = Objects.requireNonNull(client, "client is null");
            this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        }
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            Objects.requireNonNull(subscription, "subscription is null");
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }
        @Override
        public void onNext(ByteBuffer item) {
            Objects.requireNonNull(item, "item is null");
            buffers.add(item);
            try {
                client.write(buffers.get(0), this, W_HANDLER);
            } catch (WritePendingException wpe) {
                // empty
            }
        }
        @Override
        public void onError(Throwable throwable) {
            Objects.requireNonNull(throwable, "throwable is null");
            log.error("error", throwable);
            try {
                close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
            }
        }
        @Override
        public void onComplete() {
            log.debug("onComplete()");
            try {
                close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
            }
        }
        @Override
        public void close() throws IOException {
            subscription.cancel();
            client.close();
        }
        void closeUnchecked() {
            try {
                close();
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to close", ioe);
            }
        }
        final AsynchronousSocketChannel client;
        final SubmissionPublisher<ByteBuffer> publisher;
        private Flow.Subscription subscription;
    }
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, ChatTcp3ServerAttachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, ChatTcp3ServerAttachment attachment) {
            assert !attachment.buffers.isEmpty();
            var buffer = attachment.buffers.get(0);
            if (!buffer.hasRemaining()) {
                attachment.buffers.remove(0);
            }
            if (!attachment.buffers.isEmpty()) {
                attachment.client.write(
                        attachment.buffers.get(0), // <src>
                        attachment,                // <attachment>
                        this                       // <handler>
                );
            }
        }
        @Override public void failed(Throwable exc, ChatTcp3ServerAttachment attachment) {
            log.error("failed to write", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, ChatTcp3ServerAttachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, ChatTcp3ServerAttachment attachment) {
            if (result == -1) {
                attachment.closeUnchecked();
                return;
            }
            if (!attachment.buffer.hasRemaining()) {
                attachment.publisher.submit(_ChatMessage.OfBuffer.copyOf(attachment.buffer));
                attachment.buffer.clear();
            }
            attachment.client.read(attachment.buffer, attachment, this);
        }
        @Override public void failed(Throwable exc, ChatTcp3ServerAttachment attachment) {
            log.error("failed to read", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var server = AsynchronousServerSocketChannel.open();
             var publisher = new SubmissionPublisher<ByteBuffer>()) {
            server.bind(new InetSocketAddress(InetAddress.getByName("::"), _ChatConstants.PORT));
            log.debug("server bound to {}", server.getLocalAddress());
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        group.shutdownNow();
                        return null;
                    },
                    l -> {                         // <consumer>
                    }
            );
            server.<ChatTcp3ServerAttachment>accept(
                    null,
                    new CompletionHandler<>() { // @formatter:off
                        @Override public void completed(AsynchronousSocketChannel result,
                                                        ChatTcp3ServerAttachment attachment) {
                            try {
                                log.debug("accepted from {}, through {}", result.getRemoteAddress(),
                                          result.getLocalAddress());
                            } catch (IOException ioe) {
                                log.error("failed to get addresses from {}", result, ioe);
                            }
                            attachment = new ChatTcp3ServerAttachment(result, publisher);
                            attachment.publisher.subscribe(attachment);
                            attachment.client.read(attachment.buffer, attachment, R_HANDLER);
                            server.accept(attachment, this);
                        }
                        @Override public void failed(Throwable exc, ChatTcp3ServerAttachment attachment) {
                            log.error("failed to accept", exc);
                            try { group.shutdownNow(); } catch (IOException ioe) {
                                log.error("failed shutdown " + group, ioe);
                            }
                        } // @formatter:off
                    });
            while (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                // does nothing but just waiting...
            }
        }
    }

    private ChatTcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
