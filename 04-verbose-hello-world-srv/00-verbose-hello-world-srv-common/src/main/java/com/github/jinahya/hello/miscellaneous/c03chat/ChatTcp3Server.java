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

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;

@Slf4j
class ChatTcp3Server {

    private static final List<Flow.Subscriber<? super ByteBuffer>> SUBSCRIBERS =
            new CopyOnWriteArrayList<>();

    // @formatter:off
    private static final Flow.Publisher<ByteBuffer> PUBLISHER = subscriber -> {
        Objects.requireNonNull(subscriber, "subscriber is null");
        subscriber.onSubscribe(new Flow.Subscription() {
            @Override public void request(long n) { SUBSCRIBERS.add(subscriber); }
            @Override public void cancel() { SUBSCRIBERS.remove(subscriber); }
        });
    };
    // @formatter:on

    // @formatter:off
    static class Attachment implements Flow.Subscriber<ByteBuffer> {
        Attachment(final AsynchronousServerSocketChannel server) {
            this.server = Objects.requireNonNull(server, "server is null");
        }
        @Override public void onSubscribe(Flow.Subscription subscription) {
            log.debug("onSubscribe({})", subscription);
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }
        @Override public void onNext(ByteBuffer item) {
            log.debug("onNext({})", item);
            wrapped.buffers.add(item);
            try {
                client.write(wrapped.buffers.get(0), this, W_HANDLER);
            } catch (WritePendingException wpe) {
                // empty
            }
        }
        @Override public void onError(Throwable throwable) {
            log.error("[S] onError({})", throwable, throwable);
        }
        @Override public void onComplete() {
            log.debug("[S] onComplete()");
        }
        private AsynchronousServerSocketChannel server;
        private Flow.Subscription subscription;
        private AsynchronousSocketChannel client;
        final ChatTcp2Server.Attachment wrapped = new ChatTcp2Server.Attachment();
    }
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            assert !attachment.wrapped.buffers.isEmpty();
            var i = attachment.wrapped.buffers.iterator();
            assert i.hasNext();
            var buffer = i.next();
            if (!buffer.hasRemaining()) {
                i.remove();
            }
            if (i.hasNext()) {
                buffer = i.next();
                attachment.client.write(buffer, attachment, this);
            }
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("[S] failed to write", exc);
            attachment.subscription.cancel();
            try {
                attachment.client.close();
            } catch (IOException ioe) {
                log.error("[S] failed to close {}", attachment.client, ioe);
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                attachment.subscription.cancel();
                try {
                    attachment.client.close();
                } catch (IOException ioe) {
                    throw new UncheckedIOException("[S] failed to close" + attachment.client, ioe);
                }
                return;
            }
            if (!attachment.wrapped.buffer.hasRemaining()) {
                SUBSCRIBERS.forEach(s -> s.onNext(_ChatMessage.copy(attachment.wrapped.buffer)));
                attachment.wrapped.buffer.clear();
            }
            attachment.client.read(attachment.wrapped.buffer, attachment, this);
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("[S] failed to read", exc);
            attachment.subscription.cancel();
            try {
                attachment.client.close();
            } catch (IOException ioe) {
                log.error("[S] failed to close {}", attachment.client, ioe);
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.debug("[S] accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to get addresses from client", ioe);
            }
            PUBLISHER.subscribe(attachment);
            attachment.client = result;
            attachment.client.read(attachment.wrapped.buffer, attachment, R_HANDLER);
            attachment.server.accept(new Attachment(attachment.server), this);
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            // empty
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT
            ));
            log.debug("[S] server bound to {}", server.getLocalAddress());
            var latch = new CountDownLatch(1);
            HelloWorldLangUtils.callWhenRead(
                    v -> !Thread.currentThread().isInterrupted(),
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        latch.countDown();
                        return null;
                    },
                    l -> {
                    }
            );
            var attachment = new Attachment(server);
            server.accept(attachment, A_HANDLER);
            latch.await();
        }
        SUBSCRIBERS.forEach(s -> {
            var subscription = ((Attachment) s).subscription;
            subscription.cancel();
            var client = ((Attachment) s).client;
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("[S] failed to close {}", client, ioe);
            }
        });
    }

    private ChatTcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
