package com.github.jinahya.hello.miscellaneous.c01rfc863;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp4Server {

    // @formatter:off
    static class Attachment extends Rfc863Tcp3Server.Attachment {
        AsynchronousSocketChannel client;
        final CountDownLatch latch = new CountDownLatch(2);
    }
    // @formatter:off

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                try {
                    attachment.client.close();
                } catch (IOException ioe) {
                    log.error("failed to close {}", attachment.client, ioe);
                }
                _Rfc863Utils.logServerBytes(attachment.bytes);
                _Rfc863Utils.logDigest(attachment.digest);
                attachment.latch.countDown(); // -1 for being all received
                return;
            }
            assert result > 0 : "buffer passed with no remaining?";
            attachment.bytes += result;
            attachment.digest.update(
                    attachment.slice
                            .position(attachment.buffer.position() - result)
                            .limit(attachment.buffer.position())
            );
            if (!attachment.buffer.hasRemaining()) {
                attachment.buffer.clear();
            }
            attachment.client.read(
                    attachment.buffer,                      // <dst>
                    _Rfc863Constants.READ_TIMEOUT_DURATION, // <timeout>
                    _Rfc863Constants.READ_TIMEOUT_UNIT,     // <unit>
                    attachment,                             // <attachment>
                    this                                    // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            try {
                attachment.client.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", attachment.client, ioe);
            }
            assert attachment.latch.getCount() == 1;
            attachment.latch.countDown();
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            attachment.client = result;
            try {
                log.info("accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to get addresses from {}", attachment.client, ioe);
            }
            attachment.latch.countDown(); // -1 for being accepted
            if (!attachment.buffer.hasRemaining()) {
                attachment.buffer.clear();
            }
            attachment.client.read(
                    attachment.buffer,                      // <dst>
                    _Rfc863Constants.READ_TIMEOUT_DURATION, // <timeout>
                    _Rfc863Constants.READ_TIMEOUT_UNIT,     // <unit>
                    attachment,                             // <attachment>
                    R_HANDLER                               // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to accept", exc);
            while (attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER   // <handler>
            );
            var broken = attachment.latch.await(_Rfc863Constants.ACCEPT_TIMEOUT_DURATION,
                                                _Rfc863Constants.ACCEPT_TIMEOUT_UNIT);
            if (!broken) {
                log.error("latch hasn't been broken!");
            }
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
