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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Server {

    static final int MAX_PACKET_LENGTH = Rfc862Udp1Server.MAX_PACKET_LENGTH;

    private static class Attachment {

        ByteBuffer buffer;

        SocketAddress address;
    }

    public static void main(String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open()) {
            try (var server = DatagramChannel.open()) {
                server.bind(_Rfc862Constants.ENDPOINT);
                log.debug("[S] bound to {}", server.getLocalAddress());
                server.configureBlocking(false);
                var attachment = new Attachment();
                attachment.buffer = ByteBuffer.allocate(MAX_PACKET_LENGTH);
                server.register(selector, SelectionKey.OP_READ, attachment);
                while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isReadable()) {
                            var channel = (DatagramChannel) key.channel();
                            var buffer = ((Attachment) key.attachment()).buffer;
                            var address = channel.receive(buffer);
                            log.debug("[S] {} byte(s) received from {}", buffer.position(),
                                      address);
                            buffer.flip();
                            ((Attachment) key.attachment()).address = address;
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                        if (key.isWritable()) {
                            var channel = (DatagramChannel) key.channel();
                            var buffer = ((Attachment) key.attachment()).buffer;
                            var address = ((Attachment) key.attachment()).address;
                            var sent = channel.send(buffer, address);
                            log.debug("[S] {} byte(s) sent back to {}", sent, address);
                            key.cancel();
                        }
                    }
                }
            }
        }
    }

    private Rfc862Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
