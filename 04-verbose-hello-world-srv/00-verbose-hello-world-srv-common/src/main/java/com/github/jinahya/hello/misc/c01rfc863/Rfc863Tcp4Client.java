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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp4Client {

    // @formatter:off
    static final class Attachment extends Rfc863Tcp3Client.Attachment {
        /**
         * Creates a new instance holds specified client.
         * @param client the client to hold.
         */
        Attachment(AsynchronousSocketChannel client) {
            super();
            this.client = Objects.requireNonNull(client, "client is null");
        }
        void write(CompletionHandler<Integer, ? super Attachment> handler) {
            Objects.requireNonNull(handler, "handler is null");
            if (!buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(buffer.array());
                buffer.clear().limit(Math.min(buffer.remaining(), bytes));
            }
            client.write(
                    buffer, // <src>
                    this,   // <attachment>
                    handler // <handler>
            );
        }
        final AsynchronousSocketChannel client;
        final CountDownLatch latch = new CountDownLatch(2);
    }
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            attachment.digest.update(
                    attachment.slice
                            .position(attachment.buffer.position() - result)
                            .limit(attachment.buffer.position())
            );
            if ((attachment.bytes -= result) == 0) {
                _Rfc863Utils.logDigest(attachment.digest);
                attachment.latch.countDown(); // -1 for all sent
                return;
            }
            if (!attachment.buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                attachment.buffer.clear().limit(
                        Math.min(attachment.buffer.remaining(), attachment.bytes)
                );
            }
            attachment.client.write(
                    attachment.buffer, // <src>
                    attachment,        // <attachment>
                    this               // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            assert attachment.latch.getCount() == 1;
            attachment.latch.countDown();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Void, Attachment> C_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Void result, Attachment attachment) {
            try {
                log.info("connected to {}, through {}", attachment.client.getRemoteAddress(),
                          attachment.client.getLocalAddress());
            } catch (IOException ioe) {
                log.error("failed to get addresses from {}", attachment.client, ioe);
            }
            attachment.latch.countDown(); // -1 for being connected to the server
            if (!attachment.buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                attachment.buffer.clear().limit(
                        Math.min(attachment.buffer.remaining(), attachment.bytes)
                );
            }
            attachment.client.write(
                    attachment.buffer, // <src>
                    attachment,        // attachment>
                    W_HANDLER          // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to connect", exc);
            while (attachment.latch.getCount() > 0) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment(client);
            attachment.client.connect(
                    _Rfc863Constants.ADDR, // <remote>
                    attachment,            // <attachment>
                    C_HANDLER              // <handler>
            );
            attachment.latch.await();
        }
    }

    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
