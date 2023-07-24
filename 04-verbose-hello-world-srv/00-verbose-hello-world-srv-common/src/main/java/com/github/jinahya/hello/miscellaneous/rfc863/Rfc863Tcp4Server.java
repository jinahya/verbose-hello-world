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

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863Tcp4Server {

    static final InetAddress HOST = Rfc863Tcp3Server.HOST;

    static final int PORT = Rfc863Tcp3Server.PORT;

    private static CompletionHandler<Integer, ByteBuffer> writer(
            AsynchronousSocketChannel client, CountDownLatch latch) {
        return new CompletionHandler<>() {
            @Override
            public void completed(Integer written, ByteBuffer src) {
                log.debug("[S] written: {}", written);
                if (!src.hasRemaining()) {
                    latch.countDown();
                    return;
                }
                client.write(src, 8L, TimeUnit.SECONDS, src, this);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer src) {
                log.error("failed to write", exc);
                latch.countDown();
            }
        };
    }

    private static CompletionHandler<AsynchronousSocketChannel, ByteBuffer> accepter(
            CountDownLatch latch) {
        return new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel client, ByteBuffer src) {
                try {
                    log.error("[S] accepted from {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                } catch (final IOException ioe) {
                    log.error("failed to get addresses from {}", client, ioe);
                }
                client.write(src, 8L, TimeUnit.SECONDS, src, writer(client, latch));
            }

            @Override
            public void failed(Throwable exc, ByteBuffer src) {
                log.error("[S] failed to accept", exc);
            }
        };
    }

    public static void main(String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalAddress());
            var latch = new CountDownLatch(1);
            server.accept(
                    ByteBuffer.allocate(6), // <attachment>
                    accepter(latch)         // <handler>
            );
            var broken = latch.await(8, TimeUnit.SECONDS);
            log.debug("[S] closing server...");
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
