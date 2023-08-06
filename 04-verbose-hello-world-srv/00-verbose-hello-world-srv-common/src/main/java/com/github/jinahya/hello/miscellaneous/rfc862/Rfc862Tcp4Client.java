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

import com.github.jinahya.hello.util.HelloWorldSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp4Client {

    private static class Attachment extends Rfc862Tcp4Server.Attachment {

        Attachment() {
            super();
            bytes = ThreadLocalRandom.current().nextInt(1048576);
        }
    }

    // @formatter:on
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override
        public void completed(Integer result, Attachment attachment) {
            log.trace("[C] - read: {}", result);
            if (result == -1) {
                try {
                    attachment.client.shutdownInput();
                } catch (IOException ioe) {
                    throw new UncheckedIOException("failed to shutdown input", ioe);
                }
                attachment.latch.countDown();
                return;
            } else {
                HelloWorldSecurityUtils.updatePreceding(
                        attachment.digest, attachment.buffer, result
                );
            }
            if (attachment.latch.getCount() == 2) { // not all written
                attachment.buffer
                        .limit(attachment.buffer.position() - result)
                        .position(0);
                if (!attachment.buffer.hasRemaining()) {
                    attachment.buffer.clear();
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    attachment.buffer.limit(
                            Math.min(attachment.buffer.capacity(), attachment.bytes)
                    );
                }
                attachment.client.write(
                        attachment.buffer,    // <src>
                        8L, TimeUnit.SECONDS, // <timeout, unit>
                        attachment,           // <attachment>
                        W_HANDLER             // <handler
                );
                return;
            }
            assert attachment.latch.getCount() == 1; // all written
            attachment.buffer.clear();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    this                  // <handler>
            );
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            log.trace("[C] - written: {}", result);
            attachment.bytes -= result;
            if (attachment.bytes == 0) {
                attachment.latch.countDown();
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    throw new UncheckedIOException("failed to shutdown output", ioe);
                }
            }
            attachment.buffer.compact();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    R_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Void, Attachment> C_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Void result, Attachment attachment) {
            var client = attachment.client;
            try {
                log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
            } catch (IOException ioe) {
                throw new UncheckedIOException("failed to get addresses from client", ioe);
            }
            attachment.latch.countDown();
            ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
            client.write(
                    attachment.buffer,    // <src>
                    8L, TimeUnit.SECONDS, // <timeout, unit>
                    attachment,           // <attachment>
                    W_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to connect", exc);
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("[C] bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment();
            log.debug("[C] sending {} bytes", attachment.bytes);
            attachment.client = client;
            client.connect(
                    _Rfc862Constants.ENDPOINT, // <remote>
                    attachment,                // <attachment>
                    C_HANDLER                  // <handler>
            );
            var broken = attachment.latch.await(8L, TimeUnit.SECONDS);
            assert broken : "the latch hasn't broken!";
            log.debug("[S] digest: {}",
                      Base64.getEncoder().encodeToString(attachment.digest.digest())
            );
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
