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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Rfc862Tcp4Client {

    private static final InetAddress HOST = Rfc862Tcp4Server.HOST;

    private static final int PORT = Rfc862Tcp4Server.PORT;

    private static final int CAPACITY = Rfc862Tcp4Server.CAPACITY + 2;

    private static CompletionHandler<Integer, Void> reader(CountDownLatch latch,
                                                           AsynchronousSocketChannel client) {
        return new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Void attachment) {
                log.debug("[C] written: {}", result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                log.error("failed to connect", exc);
                latch.countDown();
            }
        };
    }

    private static CompletionHandler<Integer, Void> writer(CountDownLatch latch,
                                                           AsynchronousSocketChannel client,
                                                           ByteBuffer buffer) {
        return new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Void attachment) {
                log.debug("[C] written: {}", result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                log.error("failed to connect", exc);
                latch.countDown();
            }
        };
    }

    private static CompletionHandler<Void, Void> connector(CountDownLatch latch,
                                                           AsynchronousSocketChannel client) {
        return new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                try {
                    log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                } catch (IOException ioe) {
                    log.error("failed to get addresses from " + client, ioe);
                    failed(ioe, null);
                    return;
                }
                var buffer = ByteBuffer.allocate(CAPACITY);
                client.write(buffer, 8L, TimeUnit.SECONDS, null, writer(latch, client, buffer));
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                log.error("failed to connect", exc);
                latch.countDown();
            }
        };
    }

    public static void main(String... args)
            throws IOException, ExecutionException, InterruptedException {
        try (var client = AsynchronousSocketChannel.open()) {
            var bind = true;
            if (bind) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            var latch = new CountDownLatch(1);
            client.connect(new InetSocketAddress(HOST, PORT), null, connector(latch, client));
            var broken = latch.await(8L, TimeUnit.SECONDS);
            log.debug("[C] closing client...");
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
