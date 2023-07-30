package com.github.jinahya.hello.miscellaneous.rfc862;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp4Client {

    private static final InetAddress HOST = Rfc862Tcp4Server.HOST;

    private static final int PORT = Rfc862Tcp4Server.PORT;

    private static final int CAPACITY = Rfc862Tcp4Server.CAPACITY << 1;

    private static final String ALGORITHM = "MD5";

    private static class Attachment extends Rfc862Tcp4Server.Attachment {

        volatile int bytes = 8192;
    }

    private static final CompletionHandler<Integer, Attachment> R_HANDLER =
            new CompletionHandler<>() { // @formatter:on
                @Override
                public void completed(Integer result, Attachment attachment) {
                    log.debug("[C] -    read: {}", result);
                    if (result == -1) {
                        assert attachment.bytes == 0;
                        attachment.latch.countDown();
                        return;
                    }
                    if (attachment.latch.getCount() == 2) { // not all written
                        attachment.buffer
                                .limit(attachment.buffer.position() - result)
                                .position(0);
                        if (!attachment.buffer.hasRemaining()) {
                            attachment.buffer.clear();
                            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                            attachment.buffer.limit(
                                    Math.min(attachment.buffer.capacity(), attachment.bytes)
                            );
                        }
                        attachment.client.write(
                                attachment.buffer,    // <src>
                                8L, TimeUnit.SECONDS, // <timeout, unit>
                                attachment,           // <attachment>
                                W_HANDLER             // <handler
                        );
                        return;
                    }
                    assert attachment.latch.getCount() == 1; // all written
                    attachment.buffer.clear();
                    attachment.client.read(
                            attachment.buffer,    // <dst>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            this                  // <handler>
                    );
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to read", exc);
                } // @formatter:on
            };

    private static final CompletionHandler<Integer, Attachment> W_HANDLER =
            new CompletionHandler<>() { // @formatter:off
                @Override public void completed(Integer result, Attachment attachment) {
                    log.debug("[C] - written: {}", result);
                    attachment.bytes -= result;
                    if (attachment.bytes == 0) { // all written
                        attachment.latch.countDown();
                        try {
                            attachment.client.shutdownOutput();
                        } catch (IOException ioe) {
                            throw new UncheckedIOException("failed to shutdown output", ioe);
                        }
                    }
                    attachment.buffer.compact();
                    attachment.client.read(
                            attachment.buffer,    // <dst>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            R_HANDLER             // <handler>
                    );
                }
                @Override public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to write", exc);
                } // @formatter:on
            };

    private static final CompletionHandler<Void, Attachment> C_HANDLER =
            new CompletionHandler<>() { // @formatter:off
                @Override public void completed(Void result, Attachment attachment) {
                    var client = attachment.client;
                    try {
                        log.debug("[C] - connected to {}, through {}", client.getRemoteAddress(),
                                  client.getLocalAddress());
                    } catch (IOException ioe) {
                        throw new UncheckedIOException("failed to get addresses from client", ioe);
                    }
                    attachment.latch.countDown();
                    attachment.buffer = ByteBuffer.allocate(CAPACITY);
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    client.write(
                            attachment.buffer,    // <src>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            W_HANDLER             // <handler>
                    );
                }
                @Override public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to connect", exc);
                } // @formatter:on
            };

    public static void main(String... args)
            throws IOException, InterruptedException, NoSuchAlgorithmException {
        try (var client = AsynchronousSocketChannel.open()) {
            var bind = true;
            if (bind) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.debug("[C] client bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment();
            attachment.client = client;
            attachment.latch = new CountDownLatch(3);
            client.connect(
                    new InetSocketAddress(HOST, PORT), // <remote>
                    attachment,                        // <attachment>
                    C_HANDLER                          // <handler>
            );
            var broken = attachment.latch.await(8L, TimeUnit.SECONDS);
            assert broken;
            log.debug("[C] closing client...");
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
