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
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc862
// https://stackoverflow.com/q/23301598/330457
@Slf4j
class Rfc862Tcp4Server {

    static final InetAddress HOST = Rfc862Tcp3Server.HOST;

    static final int PORT = Rfc862Tcp3Server.PORT;

    static final int CAPACITY = 1024;

    private static final String ALGORITHM = "MD5";

    static class Attachment {

        volatile AsynchronousSocketChannel client;

        volatile CountDownLatch latch;

        volatile ByteBuffer buffer;
    }

    private static final CompletionHandler<Integer, Attachment> W_HANDLER =
            new CompletionHandler<>() { // @formatter:on
                @Override
                public void completed(Integer result, Attachment attachment) {
                    log.debug("[S] - written: {}", result);
                    if (attachment.latch.getCount() == 1) { // all read
                        if (attachment.buffer.hasRemaining()) {
                            attachment.client.write(
                                    attachment.buffer,    // <dst>
                                    8L, TimeUnit.SECONDS, // <timeout, unit>
                                    attachment,           // <attachment>
                                    this                  // <handler>
                            );
                            return;
                        }
                        attachment.latch.countDown();
                        return;
                    }
                    attachment.buffer.compact();
                    attachment.client.read(
                            attachment.buffer,    // <dst>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            R_HANDLER             // <handler>
                    );
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to write", exc);
                } // @formatter:on
            };

    private static final CompletionHandler<Integer, Attachment> R_HANDLER =
            new CompletionHandler<>() { // @formatter:on
                @Override
                public void completed(Integer result, Attachment attachment) {
                    log.debug("[S] -    read: {}", result);
                    if (result == -1) {
                        attachment.latch.countDown();
                    }
                    attachment.buffer.flip(); // limit -> position; position -> zero
                    attachment.client.write(
                            attachment.buffer,    // <src>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            W_HANDLER             // <handler>
                    );
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to read", exc);
                } // @formatter:on
            };

    private static final CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER =
            new CompletionHandler<>() { // @formatter:on
                @Override
                public void completed(AsynchronousSocketChannel result, Attachment attachment) {
                    try {
                        log.debug("[S] - accepted from {}, through {}",
                                  result.getRemoteAddress(), result.getLocalAddress());
                    } catch (IOException ioe) {
                        throw new UncheckedIOException("failed to get addresses from client", ioe);
                    }
                    attachment.latch.countDown();
                    attachment.client = result;
                    attachment.client.read(
                            attachment.buffer,    // <dst>
                            8L, TimeUnit.SECONDS, // <timeout, unit>
                            attachment,           // <attachment>
                            R_HANDLER             // <handler>
                    );
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    log.error("failed to accept", exc);
                } // @formatter:on
            };

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] server bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            attachment.latch = new CountDownLatch(3);
            attachment.buffer = ByteBuffer.allocate(CAPACITY);
//            attachment.digest = MessageDigest.getInstance(ALGORITHM);
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER    // <handler>
            );
            var broken = attachment.latch.await(8L, TimeUnit.SECONDS);
            assert broken;
            log.debug("[S] closing server...");
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
