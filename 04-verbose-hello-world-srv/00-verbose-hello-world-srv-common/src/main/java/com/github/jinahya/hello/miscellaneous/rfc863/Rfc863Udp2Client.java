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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Udp2Client {

    private static final InetAddress HOST = Rfc863Udp2Server.HOST;

    private static final int PORT = Rfc863Udp2Server.PORT;

    private static final int MAX_PACKET_LENGTH = Rfc863Udp2Server.MAX_PACKET_LENGTH;

    public static void main(String... args) throws IOException {
        try (var selector = Selector.open()) {
            try (DatagramChannel client = DatagramChannel.open()) {
                var bind = true;
                if (bind) {
                    client.bind(new InetSocketAddress(HOST, 0));
                    log.debug("[C] client bound to {}", client.getLocalAddress());
                }
                var connect = false;
                if (connect) {
                    client.connect(new InetSocketAddress(HOST, PORT));
                    log.debug("[C] connected to {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                }
                client.configureBlocking(false);
                client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        ByteBuffer.allocate(
                                ThreadLocalRandom.current().nextInt(MAX_PACKET_LENGTH) + 1)
                );
                while (!selector.keys().isEmpty()) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isWritable()) {
                            var channel = (DatagramChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var sent = channel.send(buffer, new InetSocketAddress(HOST, PORT));
                            log.debug("[C] bytes sent: {}", sent);
                            if (connect) {
                                client.disconnect();
                                log.debug("[C] client disconnected");
                            }
                            client.close();
                            key.cancel();
                        }
                    }
                }
                log.debug("[C] closing client...");
            }
            log.debug("[C] closing selector...");
        }
    }

    private Rfc863Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
