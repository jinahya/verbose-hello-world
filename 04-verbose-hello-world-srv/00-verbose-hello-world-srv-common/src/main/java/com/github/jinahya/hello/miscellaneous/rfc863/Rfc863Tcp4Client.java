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

import com.github.jinahya.hello.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp4Client {

    private static final InetAddress HOST = Rfc863Tcp4Server.HOST;

    private static final int PORT = Rfc863Tcp4Server.PORT;

    private static final int CAPACITY = Rfc863Tcp4Server.CAPACITY << 1;

    static final String ALGORITHM = Rfc863Tcp4Server.ALGORITHM;

    private static final class Attachment extends Rfc863Tcp4Server.Attachment {

    }

    // @formatter:off
    private static CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            log.trace("[C] - written: {}", result);
            HelloWorldServerUtils.updatePreceding(attachment.digest, attachment.buffer, result);
            if ((attachment.bytes -= result) == 0) {
                log.debug("[C] digest: {}", HexFormat.of().formatHex(attachment.digest.digest()));
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    log.error("[C] failed to shutdown output", ioe);
                }
                attachment.latch.countDown();
                return;
            }
            if (!attachment.buffer.hasRemaining()) {
                attachment.buffer.clear().limit(
                        Math.min(attachment.buffer.limit(), attachment.bytes)
                );
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
            }
            attachment.client.write(
                    attachment.buffer,    // <src>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    this                  // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("[C] failed to write", exc);
        }
    };
    // @formatter:on

    // @formatter:off
    private static CompletionHandler<Void, Attachment> C_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Void result, Attachment attachment) {
            try {
                log.debug("[C] connected to {}, through {}", attachment.client.getRemoteAddress(),
                          attachment.client.getLocalAddress());
            } catch (IOException ioe) {
                log.error("failed to get addresses from {}", attachment.client, ioe);
            }
            attachment.latch.countDown();
            attachment.client.write(
                    attachment.buffer,    // <src>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // attachment>
                    W_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("[C] failed to connect", exc);
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            var bind = true;
            if (bind) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment();
            attachment.client = client;
            attachment.latch = new CountDownLatch(2);
            attachment.buffer = ByteBuffer.allocate(CAPACITY);
            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
            attachment.bytes = ThreadLocalRandom.current().nextInt(1048576);
            log.debug("[C] byte(s) to send: {}", attachment.bytes);
            attachment.buffer.limit(Math.min(attachment.buffer.limit(), attachment.bytes));
            attachment.digest = MessageDigest.getInstance(ALGORITHM);
            attachment.client.connect(
                    new InetSocketAddress(HOST, PORT), // <remote>
                    attachment,                        // <attachment>
                    C_HANDLER                          // <handler>
            );
            var broken = attachment.latch.await(8L, TimeUnit.SECONDS);
            assert broken;
        }
    }

    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
