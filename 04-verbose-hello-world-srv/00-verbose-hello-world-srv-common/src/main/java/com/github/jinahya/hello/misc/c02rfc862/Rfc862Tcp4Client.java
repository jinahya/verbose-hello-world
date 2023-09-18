package com.github.jinahya.hello.misc.c02rfc862;

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

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp4Client {

    // @formatter:on
    private static class Attachment extends Rfc862Tcp3ClientAttachment {

        Attachment(AsynchronousChannelGroup group, AsynchronousSocketChannel client) {
            super();
            this.group = Objects.requireNonNull(group, "group is null");
            this.client = Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void close() throws IOException {
            group.shutdownNow();
            super.close();
        }

        private final AsynchronousChannelGroup group;

        private final AsynchronousSocketChannel client;
    }
    // @formatter:on

    // @formatter:on
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                if (attachment.bytes > 0) {
                    throw new UncheckedIOException(new EOFException("unexpected eof"));
                }
                attachment.closeUnchecked();
                return;
            }
            if (attachment.bytes == 0) { // all bytes have already been sent
                attachment.buffer.clear();
                assert attachment.buffer.hasRemaining();
                attachment.client.read(
                        attachment.buffer,                      // <dst>
                        _Rfc862Constants.READ_TIMEOUT_DURATION, // <timeout>
                        _Rfc862Constants.READ_TIMEOUT_UNIT,     // <unit>
                        attachment,                             // <attachment>
                        this                                    // <handler>
                );
                return;
            }
            attachment.buffer
                    .limit(attachment.buffer.capacity())
                    .position(attachment.buffer.limit());
            if (!attachment.buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                attachment.buffer.clear().limit(
                        Math.min(attachment.buffer.remaining(), attachment.bytes)
                );
            }
            assert attachment.buffer.hasRemaining();
            attachment.client.write(
                    attachment.buffer, // <dst>
                    attachment,        // <attachment>
                    W_HANDLER          // <handler>
            );
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to read", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    // @formatter:on
    private static final
    CompletionHandler<Integer, Attachment> W_HANDLER = new CompletionHandler<>() {
        @Override
        public void completed(Integer result, Attachment attachment) {
            assert result > 0;
            attachment.digest.update(
                    attachment.slice
                            .position(attachment.buffer.position() - result)
                            .limit(attachment.buffer.position())
            );
            if ((attachment.bytes -= result) == 0) { // all bytes have been sent
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    throw new UncheckedIOException(
                            "failed to shutdown output of " + attachment.client, ioe);
                }
                _Rfc862Utils.logDigest(attachment.digest);
//                attachment.buffer
//                        .limit(attachment.buffer.capacity())
//                        .position(attachment.buffer.limit());
            }
            attachment.buffer.flip();
            assert attachment.buffer.hasRemaining();
            attachment.client.read(
                    attachment.buffer,                      // <dst>
                    _Rfc862Constants.READ_TIMEOUT_DURATION, // <timeout>
                    _Rfc862Constants.READ_TIMEOUT_UNIT,     // <unit>
                    attachment,                             // <attachment>
                    R_HANDLER                               // <handler>
            );
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            attachment.closeUnchecked();
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
            if (!attachment.buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                attachment.buffer.clear().limit(
                                Math.min(attachment.buffer.remaining(), attachment.bytes)
                );
            }
            attachment.client.write(
                    attachment.buffer, // <src>
                    attachment,        // <attachment>
                    W_HANDLER          // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to connect", exc);
            attachment.closeUnchecked();
        }
    };
    // @formatter:on

    public static void main(String... args) throws Exception {
        var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var client = AsynchronousSocketChannel.open(group)) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            var attachment = new Attachment(group, client);
            client.connect(
                    _Rfc862Constants.ADDR, // <remote>
                    attachment,            // <attachment>
                    C_HANDLER              // <handler>
            );
            if (!group.awaitTermination(_Rfc862Constants.ACCEPT_TIMEOUT_DURATION,
                                        _Rfc862Constants.ACCEPT_TIMEOUT_UNIT)) {
                log.error("channel group has not been terminated!");
            }
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
