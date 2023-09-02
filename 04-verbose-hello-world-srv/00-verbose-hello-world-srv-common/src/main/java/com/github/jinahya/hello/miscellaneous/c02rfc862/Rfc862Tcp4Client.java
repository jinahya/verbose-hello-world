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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp4Client {

    // @formatter:off
    private static class Attachment extends Rfc862Tcp4Server.Attachment {
        Attachment() {
            super();
            bytes = _Rfc862Utils.randomBytesLessThanOneMillion();
            _Rfc862Utils.logClientBytes(bytes);
            buffer.position(buffer.limit());
        }
    }
    // @formatter:on

    // @formatter:on
    private static final
    CompletionHandler<Integer, Attachment> R_HANDLER = new CompletionHandler<>() {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                assert attachment.bytes == 0 : "unexpected end-of-stream";
                attachment.shutdownGroupNow();
                return;
            }
            if (attachment.bytes == 0) { // all bytes have already been sent
                attachment.slice.position(0).limit(attachment.buffer.position());
                attachment.client.read(
                        attachment.slice, // <dst>
                        attachment,       // <attachment>
                        this              // <handler>
                );
                return;
            }
            if (!attachment.buffer.hasRemaining()) {
                ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                attachment.buffer.clear().limit(Math.min(
                        attachment.buffer.remaining(), attachment.bytes
                ));
            }
            attachment.client.write(
                    attachment.buffer, // <dst>
                    attachment,        // <attachment>
                    W_HANDLER          // <handler>
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
            attachment.digest.update(
                    attachment.slice
                            .position(attachment.buffer.position() - result)
                            .limit(attachment.buffer.position())
            );
            if ((attachment.bytes -= result) == 0) { // all bytes have been sent
                try {
                    attachment.client.shutdownOutput();
                } catch (IOException ioe) {
                    log.error("failed to shutdown output of {}", attachment.client, ioe);
                }
                _Rfc862Utils.logDigest(attachment.digest);
                attachment.buffer
                        .limit(attachment.buffer.capacity())
                        .position(attachment.buffer.limit());
            }
            attachment.slice.position(0).limit(attachment.buffer.position());
            attachment.client.read(
                    attachment.slice, // <dst>
                    attachment,       // <attachment>
                    R_HANDLER         // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to write", exc);
            attachment.shutdownGroupNow();
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
                attachment.buffer.clear().limit(Math.min(
                        attachment.buffer.remaining(), attachment.bytes
                ));
            }
            attachment.client.write(
                    attachment.buffer, // <src>
                    attachment,        // <attachment>
                    W_HANDLER          // <handler>
            );
        }
        @Override public void failed(Throwable exc, Attachment attachment) {
            log.error("failed to connect", exc);
            attachment.shutdownGroupNow();
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
            var attachment = new Attachment();
            attachment.group = group;
            attachment.client = client;
            client.connect(
                    _Rfc862Constants.ADDR, // <remote>
                    attachment,               // <attachment>
                    C_HANDLER                 // <handler>
            );
            if (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                log.error("channel group has not been terminated, for a while");
            }
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
