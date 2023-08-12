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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HexFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp4Server {

    static class Attachment extends Rfc863Tcp2Server.Attachment {

        CountDownLatch latch;

        AsynchronousSocketChannel client;
    }

    // @formatter:off
    private static CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            log.trace("[S] - read: {}", result);
            if (result == -1) {
                try {
                    attachment.client.close();
                } catch (IOException ioe) {
                    log.error("failed to close client", ioe);
                }
                log.debug("[S] byte(s) received (and discarded): {}", attachment.bytes);
                log.debug("[S] digest: {}", HexFormat.of().formatHex(attachment.digest.digest()));
                attachment.latch.countDown();
                return;
            }
            attachment.bytes += result;
            HelloWorldSecurityUtils.updatePreceding(attachment.digest, attachment.buffer, result);
            if (!attachment.buffer.hasRemaining()) {
                attachment.buffer.clear(); // position -> zero; limit -> capacity
            }
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    this                  // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
        }
    };
    // @formatter:on

    // @formatter:off
    private static
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.debug("[S] accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to get addresses from " + result, ioe);
            }
            attachment.client = result;
            attachment.latch.countDown();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    R_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("[S] failed to accept", exc);
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc863Constants.ENDPOINT);
            log.debug("[S] bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            attachment.client = null;
            attachment.latch = new CountDownLatch(2);
            attachment.bytes = 0;
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER   // <handler>
            );
            var broken = attachment.latch.await(8, TimeUnit.SECONDS);
            assert broken;
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
