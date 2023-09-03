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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

@Slf4j
class Rfc863Tcp3Client {

    // @formatter:off
    static class Attachment extends Rfc863Tcp2Client.Attachment {
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(AsynchronousSocketChannel.class, client);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.connect(_Rfc863Constants.ADDR).get(_Rfc863Constants.CONNECT_TIMEOUT_DURATION,
                                                      _Rfc863Constants.CONNECT_TIMEOUT_UNIT);
            log.info("connected to {}, through {}", client.getRemoteAddress(),
                     client.getLocalAddress());
            var attachment = new Attachment();
            for (int w; attachment.bytes > 0; ) {
                if (!attachment.buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(attachment.buffer.array());
                    attachment.buffer.clear().limit(
                            Math.min(attachment.buffer.remaining(), attachment.bytes)
                    );
                }
                assert attachment.buffer.hasRemaining();
                w = client.write(attachment.buffer).get();
                assert w >= 0;
                attachment.bytes -= w;
                attachment.digest.update(
                        attachment.slice
                                .position(attachment.buffer.position() - w)
                                .limit(attachment.buffer.position())
                );
            }
            _Rfc863Utils.logDigest(attachment.digest);
        }
    }

    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
