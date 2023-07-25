package com.github.jinahya.hello.miscellaneous.rfc863;

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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
class Rfc863Tcp4Server {

    static final InetAddress HOST = Rfc863Tcp3Server.HOST;

    static final int PORT = Rfc863Tcp3Server.PORT;

    private static CompletionHandler<Integer, Void> reader(
            CountDownLatch latch, AsynchronousSocketChannel client, ByteBuffer dst) {
        return new CompletionHandler<>() { // @formatter:off
            @Override public void completed(Integer result, Void attachment) {
                log.debug("[S] read: {}", result);
                if (result == -1) {
                    log.debug("[S] closing client...");
                    try {
                        client.close();
                    } catch (IOException ioe) {
                        throw new UncheckedIOException("failed to close " + client, ioe);
                    }
                    latch.countDown();
                    return;
                }
                client.read(
                        dst.clear(),      // <dst>
                        8L,               // <timeout>
                        TimeUnit.SECONDS, // <unit>
                        attachment,       // <attachment>
                        this              // <handler>
                );
            }
            @Override public void failed(Throwable exc, Void attachment) {
                log.error("failed to read", exc);
                log.debug("[S] closing client...");
                try {
                    client.close();
                } catch (IOException ioe) {
                    throw new UncheckedIOException("failed to close " + client, ioe);
                }
                latch.countDown();
            } // @formatter:on
        };
    }

    private static CompletionHandler<AsynchronousSocketChannel, Void> accepter(
            CountDownLatch latch) {
        return new CompletionHandler<>() { // @formatter:off
            @Override public void completed(AsynchronousSocketChannel client, Void src) {
                try {
                    log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                } catch (final IOException ioe) {
                    latch.countDown();
                    throw new UncheckedIOException("failed to get addresses from " + client, ioe);
                }
                var dst = ByteBuffer.allocate(6);
                client.read(
                        dst,                       // <dst>
                        8L,                        // <timeout>
                        TimeUnit.SECONDS,          // <unit>
                        null,                      // <attachment>
                        reader(latch, client, dst) // <handler>
                );
            }
            @Override public void failed(Throwable exc, Void src) {
                log.error("[S] failed to accept", exc);
                latch.countDown();
            } // @formatter:off
        };
    }

    public static void main(String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalAddress());
            var latch = new CountDownLatch(1);
            server.accept(
                    null,           // <attachment>
                    accepter(latch) // <handler>
            );
            var broken = latch.await(8, TimeUnit.SECONDS);
            log.debug("[S] closing server...");
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
