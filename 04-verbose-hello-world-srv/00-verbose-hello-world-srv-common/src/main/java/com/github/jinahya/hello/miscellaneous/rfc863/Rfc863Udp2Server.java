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
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863Udp2Server {

    static final InetAddress HOST = Rfc863Udp1Server.HOST;

    static final int PORT = Rfc863Udp1Server.PORT;

    static final int MAX_PACKET_LENGTH = Rfc863Udp1Server.MAX_PACKET_LENGTH;

    public static void main(String... args) throws IOException {
        try (var selector = Selector.open()) {
            try (var server = DatagramChannel.open()) {
                server.bind(new InetSocketAddress(HOST, PORT));
                log.debug("[S] bound to {}", server.getLocalAddress());
                server.configureBlocking(false);
                server.register(selector, SelectionKey.OP_READ,
                                ByteBuffer.allocate(MAX_PACKET_LENGTH));
                while (!selector.keys().isEmpty()) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isReadable()) {
                            var channel = (DatagramChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var source = channel.receive(buffer);
                            log.debug("[S] {} byte(s) received from {}", buffer.position(), source);
                            log.debug("[S] closing client...");
                            channel.close();
                            key.cancel();
                        }
                    }
                }
                log.debug("[S] closing server...");
            }
            log.debug("[S] closing selector...");
        }
    }

    private Rfc863Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
