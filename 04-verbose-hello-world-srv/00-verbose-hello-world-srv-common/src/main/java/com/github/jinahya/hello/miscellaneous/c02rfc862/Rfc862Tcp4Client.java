package com.github.jinahya.hello.miscellaneous.c02rfc862;

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

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp4Client {

    private static class Attachment
            extends Rfc862Tcp4Server.Attachment {

        Attachment() {
            super();
            bytes = ThreadLocalRandom.current().nextInt(1048576);
            buffer.position(buffer.limit());
        }
    }

    // @formatter:on
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                if (attachment.bytes > 0) {
                    failed(new EOFException("unexpected eof"), attachment);
                    return;
                }
                try {
                    attachment.client.shutdownInput();
                } catch (IOException ioe) {
                    failed(new UncheckedIOException("failed to shutdown input", ioe), attachment);
                    return;
                }
                attachment.latch.countDown(); // -1 for all read
                return;
            }
            if (attachment.latch.getCount() == 2) { // not all written
                attachment.buffer
                        .limit(attachment.buffer.position() - result)
                        .position(0);
                if (!attachment.buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    attachment.buffer.clear().limit(Math.min(
                            attachment.buffer.remaining(), attachment.bytes
                    ));
                }
                attachment.client.write(
                        attachment.buffer,    // <src>
                        attachment,           // <attachment>
                        W_HANDLER             // <handler
                );
                return;
            }
            attachment.buffer.clear();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    attachment,           // <attachment>
                    this                  // <handler>
            );
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            while (attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Integer result, Attachment attachment) {
            HelloWorldSecurityUtils.updatePreceding(
                    attachment.digest, attachment.buffer, result
            );
            if ((attachment.bytes -= result) == 0) {
                attachment.latch.countDown(); // -1 for all written
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    failed(ioe, attachment);
                    return;
                }
                _Rfc862Utils.logDigest(attachment.digest);
            }
            attachment.buffer.compact();
            attachment.client.read(
                    attachment.buffer,    // <dst>
                    attachment,           // <attachment>
                    R_HANDLER             // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            while (attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    // @formatter:off
    private static final
    CompletionHandler<Void, Attachment> C_HANDLER = new CompletionHandler<>() {
        @Override public void completed(Void result, Attachment attachment) {
            try {
                log.debug("connected to {}, through {}", attachment.client.getRemoteAddress(),
                          attachment.client.getRemoteAddress());
            } catch (IOException ioe) {
                failed(ioe, attachment);
                return;
            }
            attachment.latch.countDown(); // -1 for connected
            attachment.bytes = ThreadLocalRandom.current().nextInt(1048576);
            _Rfc862Utils.logClientBytesSending(attachment.bytes);
            if (attachment.bytes > 0) {
                if (!attachment.buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    attachment.buffer.clear().limit(Math.min(
                            attachment.buffer.remaining(), attachment.bytes
                    ));
                }
                attachment.client.write(
                        attachment.buffer,    // <src>
                        attachment,           // <attachment>
                        W_HANDLER             // <handler>
                );
                return;
            }
            attachment.latch.countDown(); // all written
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to connect", exc);
            while (attachment.latch.getCount() > 0L) {
                attachment.latch.countDown();
            }
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.debug("bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment();
            attachment.buffer.position(attachment.buffer.limit());
            attachment.client = client;
            client.connect(
                    _Rfc862Constants.ADDRESS, // <remote>
                    attachment,                // <attachment>
                    C_HANDLER                  // <handler>
            );
            attachment.latch.await();
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
