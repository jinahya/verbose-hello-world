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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Udp2Client {

    private static final int CAPACITY =
            ThreadLocalRandom.current().nextInt(Rfc863Udp2Server.CAPACITY);

    public static void main(String... args) throws IOException {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR, 0));
                log.debug("[C] client bound to {}", client.getLocalAddress());
            }
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc863Constants.ENDPOINT);
                log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
            }
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(CAPACITY));
            var digest = _Rfc863Utils.newMessageDigest();
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isWritable()) {
                        var channel = (DatagramChannel) key.channel();
                        var buffer = (ByteBuffer) key.attachment();
                        var sent = channel.send(buffer, _Rfc863Constants.ENDPOINT);
                        log.debug("[C] byte(s) sent: {}", sent);
                        HelloWorldSecurityUtils.updatePreceding(digest, buffer, sent);
                        if (!buffer.hasRemaining()) {
                            key.cancel();
                        }
                    }
                }
            }
            log.debug("[C] digest: {}", HexFormat.of().formatHex(digest.digest()));
            if (connect) {
                client.disconnect();
                log.debug("[C] disconnected");
            }
        }
    }

    private Rfc863Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
