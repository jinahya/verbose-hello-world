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
import com.github.jinahya.hello.miscellaneous.c03chat._ChatMessage.OfBuffer;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

@Slf4j
class ChatTcp3Server {

    // @formatter:on
    private static class Attachment
            implements Closeable,
                       Flow.Subscriber<ByteBuffer> {

        private Attachment(AsynchronousServerSocketChannel server,
                           SubmissionPublisher<ByteBuffer> publisher) {
            super();
            this.server = Objects.requireNonNull(server, "server is null");
            this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        @Override
        public void close()
                throws IOException {
            subscription.cancel();
            client.close();
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            wrapped.buffers.add(item);
            try {
                client.write(wrapped.buffers.get(0), this, W_HANDLER);
            } catch (WritePendingException wpe) {
                // empty
            }
        }

        @Override
        public void onError(Throwable throwable) {
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

        private final AsynchronousServerSocketChannel server;

        private final SubmissionPublisher<ByteBuffer> publisher;

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
            if (!attachment.client.isOpen()) {
                log.error("failed to write", exc);
            }
            try {
                attachment.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", attachment, ioe);
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                try {
                    attachment.close();
                } catch (IOException ioe) {
                    throw new UncheckedIOException("failed to close" + attachment, ioe);
                }
                return;
            }
            if (!attachment.wrapped.buffer.hasRemaining()) {
                attachment.publisher.submit(OfBuffer.copyOf(attachment.wrapped.buffer));
                attachment.wrapped.buffer.clear();
            }
            attachment.client.read(attachment.wrapped.buffer, attachment, this);
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            if (!attachment.client.isOpen()) {
                log.error("failed to read", exc);
            }
            try {
                attachment.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", attachment, ioe);
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.debug("accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to get addresses from client", ioe);
            }
            attachment.publisher.subscribe(attachment);
            attachment.client = result;
            attachment.client.read(attachment.wrapped.buffer, attachment, R_HANDLER);
            attachment.server.accept(new Attachment(attachment.server, attachment.publisher), this);
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            if (!attachment.server.isOpen()) {
                log.error("failed to accept", exc);
            }
            attachment.publisher.close();
            try {
                attachment.server.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", attachment.server, ioe);
            }
        }
    };
    // @formatter:on

    public static void main(String... args)
            throws Exception {
        try (var server = AsynchronousServerSocketChannel.open();
             var publisher = new SubmissionPublisher<ByteBuffer>()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT
            ));
            log.debug("server bound to {}", server.getLocalAddress());
            var latch = new CountDownLatch(1);
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        latch.countDown();
                        return null;
                    },
                    l -> {                         // <consumer>
                    }
            );
            server.accept(new Attachment(server, publisher), A_HANDLER);
            latch.await();
        }
        log.debug("publisher closed");
    }

    private ChatTcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
