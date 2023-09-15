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

    // @formatter:on
    static final class Attachment extends _Rfc863Attachment.Client {

        /**
         * Creates a new instance holds specified client.
         *
         * @param client the client to hold.
         */
        Attachment(final AsynchronousSocketChannel client) {
            super();
            this.client = Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void close() throws IOException {
            client.close();
            super.close();
        }

        void connectWith(final CompletionHandler<Void, ? super Attachment> handler) {
            Objects.requireNonNull(handler, "handler is null");
            client.connect(
                    _Rfc863Constants.ADDR, // <remote>
                    this,                  // <attachment>
                    handler                // <handler>
            );
        }

        void writeWith(final CompletionHandler<Integer, ? super Attachment> handler) {
            Objects.requireNonNull(handler, "handler is null");
            client.write(
                    getBufferForWriting(), // <src>
                    this,                  // <attachment>
                    handler                // <handler>
            );
        }

        private final AsynchronousSocketChannel client;
    }
    // @formatter:on

    private static final CountDownLatch LATCH = new CountDownLatch(2); // connected + all sent

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(final Integer result, final Attachment attachment) {
            attachment.updateDigest(result);
            if (attachment.decreaseBytes(result) == 0) {
                attachment.logDigest();
                assert LATCH.getCount() == 1;
                LATCH.countDown(); // -1 for all sent
                return;
            }
            attachment.writeWith(this);
        }
        @Override public void failed(final Throwable exc, final Attachment attachment) {
            log.error("failed to write", exc);
            assert LATCH.getCount() == 1;
            LATCH.countDown();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Void, Attachment> C_HANDLER = new CompletionHandler<>() {
        @Override public void completed(final Void result, final Attachment attachment) {
            try {
                log.info("connected to {}, through {}", attachment.client.getRemoteAddress(),
                          attachment.client.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to get addresses from {}", attachment.client, ioe);
            }
            assert LATCH.getCount() == 2;
            LATCH.countDown(); // -1 for being connected to the server
            assert LATCH.getCount() == 1;
            attachment.writeWith(W_HANDLER);
        }
        @Override public void failed(final Throwable exc, final Attachment attachment) {
            log.error("failed to connect", exc);
            assert LATCH.getCount() == 2;
            LATCH.countDown();
            LATCH.countDown();
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        log.debug("{}", LATCH.getCount());
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            try (var attachment = new Attachment(client)) {
                log.debug("???????: {}", LATCH.getCount());
                attachment.connectWith(C_HANDLER);
                LATCH.await();
                log.debug("???????: {}", LATCH.getCount());
            }
        }
    }

    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
