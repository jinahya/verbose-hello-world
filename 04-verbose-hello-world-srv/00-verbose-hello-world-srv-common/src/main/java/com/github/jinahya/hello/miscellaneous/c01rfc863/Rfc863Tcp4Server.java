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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

@Slf4j
class Rfc863Tcp4Server {

    static class Attachment
            extends Rfc863Tcp2Server.Attachment {

        final CountDownLatch latch = new CountDownLatch(2);

        AsynchronousSocketChannel client;
    }

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                _Rfc863Utils.logServerBytes(attachment.bytes);
                _Rfc863Utils.logDigest(attachment.digest);
                try {
                    attachment.client.close();
                } catch (IOException ioe) {
                    log.error("failed to close client", ioe);
                }
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
                    attachment,           // <attachment>
                    this                  // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            while (attachment.latch.getCount() > 0) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<AsynchronousSocketChannel, Attachment> A_HANDLER = new CompletionHandler<>() {
        @Override public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                log.debug("accepted from {}, through {}", result.getRemoteAddress(),
                          result.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to get addresses from " + result, ioe);
            }
            attachment.client = result;
            attachment.latch.countDown();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    attachment,           // <attachment>
                    R_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to accept", exc);
            while (attachment.latch.getCount() > 0) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDRESS);
            log.debug("bound to {}", server.getLocalAddress());
            var attachment = new Attachment();
            server.accept(
                    attachment, // <attachment>
                    A_HANDLER   // <handler>
            );
            attachment.latch.await(); // InterruptedException
        }
    }

    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
