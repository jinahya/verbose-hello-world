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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
class Rfc863Tcp4Server {

    // @formatter:on
    static class Attachment extends _Rfc863Attachment.Server {

        Attachment(final AsynchronousSocketChannel client) {
            super();
            this.client = Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void close() throws IOException {
            client.close();
            super.close();
        }

        void readWith(final CompletionHandler<Integer, ? super Attachment> handler) {
            Objects.requireNonNull(handler, "handler is null");
            client.read(
                    getBufferForReading(),                  // <dst>
                    _Rfc863Constants.READ_TIMEOUT_DURATION, // <timeout>
                    _Rfc863Constants.READ_TIMEOUT_UNIT,     // <unit>
                    this,                                   // <attachment>
                    handler                                 // <handler>
            );
        }

        private final AsynchronousSocketChannel client;
    }
    // @formatter:off

    private static final CountDownLatch LATCH = new CountDownLatch(2); // accepted + all received

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(final Integer result, final Attachment attachment) {
            if (result == -1) {
                attachment.logServerBytes();
                attachment.logDigest();
                attachment.closeUnchecked();
                LATCH.countDown(); // -1 for being all received
                return;
            }
            attachment.updateDigest(result);
            attachment.increaseBytes(result);
            attachment.readWith(this);
        }
        @Override public void failed(final Throwable exc, final Attachment attachment) {
            log.error("failed to read", exc);
            attachment.closeUnchecked();
            assert LATCH.getCount() == 1;
            LATCH.countDown();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Void> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(final AsynchronousSocketChannel result, final Void attachment) {
            try {
                log.info("accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to get addresses from {}", result, ioe);
            }
            LATCH.countDown(); // -1 for being accepted
            new Attachment(result).readWith(R_HANDLER);
        }
        @Override public void failed(Throwable exc, Void attachment) {
            log.error("failed to accept", exc);
            while (LATCH.getCount() > 0L) {
                LATCH.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.accept(
                    null, // <attachment>
                    A_HANDLER   // <handler>
            );
            if (!LATCH.await(_Rfc863Constants.ACCEPT_TIMEOUT_DURATION,
                             _Rfc863Constants.ACCEPT_TIMEOUT_UNIT)) {
                log.error("latch hasn't been broken!");
            }
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
