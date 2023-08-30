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

    // @formatter:on
    static class Attachment extends ChatTcp2Server.Attachment
            implements Flow.Subscriber<ByteBuffer>, Closeable {

        Attachment(AsynchronousSocketChannel client, SubmissionPublisher<ByteBuffer> publisher) {
            super();
            this.client = Objects.requireNonNull(client, "client is null");
            this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            log.debug("onSubscribe({})", subscription);
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            log.debug("onNext({})", item);
            synchronized (buffers) {
                buffers.add(item);
                try {
                    write();
                } catch (WritePendingException wpe) {
                    // empty
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("onError({})", throwable, throwable);
            closeUnchecked();
        }

        @Override
        public void onComplete() {
            log.debug("onComplete()");
            closeUnchecked();
        }

        @Override
        public void close() throws IOException {
            assert subscription != null;
            assert client != null;
            log.debug("canceling subscription...");
            subscription.cancel();
            log.debug("closing client...");
            client.close();
        }

        void closeUnchecked() {
            try {
                close();
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to close", ioe);
            }
        }

        void read() {
            client.read(
                    buffer,                     // <dst>
                    this,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override
                        public void completed(Integer result, Attachment attachment) {
                            log.debug("reading completed; result: {}, attachment: {}", result,
                                      attachment);
                            assert attachment == Attachment.this;
                            if (result == -1) {
                                closeUnchecked();
                                return;
                            }
                            if (!buffer.hasRemaining()) {
                                log.debug("submitting item...");
                                publisher.submit(_ChatMessage.OfBuffer.copyOf(buffer));
                                buffer.clear();
                            }
                            client.read(
                                    buffer,          // <dst>
                                    Attachment.this, // <attachment>
                                    this             // <handler>
                            );
                        }

                        @Override
                        public void failed(Throwable exc, Attachment attachment) {
                            assert attachment == Attachment.this;
                            log.error("failed to read", exc);
                            closeUnchecked();
                        }
                    }
            );
        }

        void write() {
            synchronized (buffers) {
                if (buffers.isEmpty()) {
                    return;
                }
                var buffer = buffers.get(0);
                client.write(
                        buffer,                     // <src>
                        this,                       // <attachment>
                        new CompletionHandler<>() { // <handler>
                            @Override
                            public void completed(Integer result, Attachment attachment) {
                                log.debug("writing completed; result: {}, attachment: {}", result,
                                          attachment);
                                assert attachment == Attachment.this;
                                synchronized (buffers) {
                                    assert !buffers.isEmpty();
                                    var buffer = buffers.get(0);
                                    if (!buffer.hasRemaining()) {
                                        buffers.remove(0);
                                    }
                                    if (!buffers.isEmpty()) {
                                        client.write(
                                                buffers.get(0),  // <src>
                                                Attachment.this, // <attachment>
                                                this             // <handler>
                                        );
                                    }
                                }
                            }

                            @Override
                            public void failed(Throwable exc, Attachment attachment) {
                                assert attachment == Attachment.this;
                                log.error("failed to write", exc);
                                attachment.closeUnchecked();
                            }
                        }

                );
            }
        }

        final AsynchronousSocketChannel client;

        final SubmissionPublisher<ByteBuffer> publisher;

        private Flow.Subscription subscription;
    }
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
                        // does nothing
                    }
            );
            // @formatter:off
            server.<Attachment>accept(
                    null,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override public void completed(AsynchronousSocketChannel result,
                                                        Attachment attachment) {
                            try {
                                log.info("accepted from {}, through {}", result.getRemoteAddress(),
                                         result.getLocalAddress());
                            } catch (IOException ioe) {
                                log.error("failed to get addresses from {}", result, ioe);
                            }
                            assert attachment == null;
                            attachment = new Attachment(result, publisher);
                            attachment.publisher.subscribe(attachment);
                            attachment.read();
                            server.accept(
                                    null, // <attachment>
                                    this  // <handler>
                            );
                        }
                        @Override public void failed(Throwable exc, Attachment attachment) {
                            log.error("failed to accept", exc);
                            assert attachment == null;
                            try { group.shutdownNow(); } catch (IOException ioe) {
                                log.error("failed shutdown {}", group, ioe);
                            }
                        }
                    });
            // @formatter:off
            while (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                // does nothing but just keep awaiting...
            }
        }
    }

    private ChatTcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
