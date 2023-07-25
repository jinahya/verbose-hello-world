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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// https://datatracker.ietf.org/doc/html/rfc862
// https://stackoverflow.com/q/23301598/330457
@Slf4j
class Rfc862Tcp4Server {

    static final InetAddress HOST = Rfc862Tcp3Server.HOST;

    static final int PORT = Rfc862Tcp3Server.PORT;

    static final int CAPACITY = Rfc862Tcp3Server.CAPACITY;

    private static CompletionHandler<Integer, Void> writer(CountDownLatch latch,
                                                           AsynchronousSocketChannel channel,
                                                           ByteBuffer buffer,
                                                           boolean recall) {
        return new CompletionHandler<>() { // @formatter:off
            @Override public void completed(Integer result, Void attachment) {
                log.debug("[S] written: {}", result);
                if (recall) {
                    buffer.compact();
                    channel.read(buffer, 8L, TimeUnit.SECONDS, null,
                                 reader(latch, channel, buffer));
                }
            }
            @Override public void failed(Throwable exc, Void attachment) {
                log.error("failed to write", exc);
                latch.countDown();
            } // @formatter:on
        };
    }

    private static CompletionHandler<Integer, Void> reader(CountDownLatch latch,
                                                           AsynchronousSocketChannel channel,
                                                           ByteBuffer buffer) {
        return new CompletionHandler<>() { // @formatter:off
            @Override public void completed(Integer result, Void attachment) {
                log.debug("[S] read: {}", result);
                if (result == -1) {
                    for (buffer.flip(); buffer.hasRemaining(); ) {
                        channel.write(buffer, 8L, TimeUnit.SECONDS, null,
                                      writer(latch, channel, buffer, false));
                    }
                    latch.countDown();
                    return;
                }
                buffer.flip(); // limit -> position; position -> zero
                channel.write(buffer, 8L, TimeUnit.SECONDS, null,
                              writer(latch, channel, buffer, true));
            }
            @Override public void failed(Throwable exc, Void attachment) {
                log.error("failed to read", exc);
                latch.countDown();
            } // @formatter:on
        };
    }

    private static CompletionHandler<AsynchronousSocketChannel, Void> acceptor(
            CountDownLatch latch) {
        return new CompletionHandler<>() { // @formatter:off
            @Override public void completed(AsynchronousSocketChannel result, Void attachment) {
                try {
                    log.debug("[S] accepted from {}, through {}", result.getRemoteAddress(),
                              result.getLocalAddress());
                } catch (IOException ioe) {
                    log.error("failed to get addresses from " + result, ioe);
                    failed(ioe, attachment);
                    return;
                }
                var buffer = ByteBuffer.allocate(CAPACITY);
                result.read(buffer, 8L, TimeUnit.SECONDS, null, reader(latch, result, buffer));
            }
            @Override public void failed(Throwable exc, Void attachment) {
                log.error("failed to accept", exc);
                latch.countDown();
            } // @formatter:on
        };
    }

    public static void main(String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalAddress());
            var latch = new CountDownLatch(1);
            server.accept(null, acceptor(latch));
            var broken = latch.await(8L, TimeUnit.SECONDS);
            log.debug("[S] closing server...");
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
