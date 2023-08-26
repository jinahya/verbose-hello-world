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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp3Client {

    // @formatter:off
    static class Attachment extends Rfc862Tcp3Server.Attachment {
        Attachment() {
            super();
            buffer.position(buffer.limit());
            bytes = _Rfc862Utils.randomBytesLessThanOneMillion();
            _Rfc862Utils.logClientBytesSending(bytes);
        }
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc862Constants.ADDRESS).get(16L, TimeUnit.SECONDS);
            log.info("connected to {}, through {}", client.getRemoteAddress(),
                     client.getLocalAddress());
            var attachment = new Attachment();
            while (attachment.bytes > 0) {
                if (!attachment.buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    attachment.buffer.clear().limit(
                            Math.min(attachment.buffer.remaining(), attachment.bytes)
                    );
                }
                var w = client.write(attachment.buffer).get();
                attachment.bytes -= w;
                attachment.digest.update(
                        attachment.slice
                                .position(attachment.buffer.position() - w)
                                .limit(attachment.buffer.position())
                );
                var r = client.read(
                        attachment.slice.position(0).limit(attachment.buffer.position())
                ).get();
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            assert attachment.bytes == 0;
            client.shutdownOutput();
            _Rfc862Utils.logDigest(attachment.digest);
            for (attachment.slice.clear(); client.read(attachment.slice).get() != -1; ) {
                if (!attachment.slice.hasRemaining()) {
                    attachment.slice.clear();
                }
            }
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
