package com.github.jinahya.hello.misc.c01rfc863;

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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp4Client {

    private static void __(final String... args) throws Exception { // @formatter:off
        try (var client = AsynchronousSocketChannel.open()) {
            final var latch = new CountDownLatch(2);
            client.connect(
                    _Rfc863Constants.ADDR,      // <remote>
                    (Void) null,                // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override
                        public void completed(Void result, Void attachment) {
                            final var src = ByteBuffer.allocate(1024);
                            ThreadLocalRandom.current().nextBytes(src.array());
                            client.write(
                                    src,                        // <src>
                                    attachment,                 // <attachment>
                                    new CompletionHandler<>() { // <handler>
                                        @Override
                                        public void completed(Integer bytes, Void attachment) {
                                            if (!src.hasRemaining()) return;
                                            client.write(
                                                    src,        // <src>
                                                    attachment, // <attachment>
                                                    this        // <handler>
                                            );
                                        }
                                        @Override
                                        public void failed(Throwable exc, Void attachment) {
                                            latch.countDown(); // 1 -> 0
                                        }
                                    }
                            );
                        }
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            latch.countDown(); // 2 -> 1
                            latch.countDown(); // 1 -> 0
                        }
                    }
            );
            latch.await();
        }
    } // @formatter:on

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            try (var attachment = new Rfc863Tcp4ClientAttachment(client)) {
                attachment.connect(Rfc863Tcp4ClientHandlers.Connect.HANDLER);
                final var broken = attachment.awaitLatch();
                assert broken;
            }
        }
    }

    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
