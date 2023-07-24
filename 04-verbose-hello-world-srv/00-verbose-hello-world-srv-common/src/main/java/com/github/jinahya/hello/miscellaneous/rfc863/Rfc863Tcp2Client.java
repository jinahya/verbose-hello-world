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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863Tcp2Client {

    private static final InetAddress HOST = Rfc863Tcp2Server.HOST;

    private static final int PORT = Rfc863Tcp2Server.PORT;

    public static void main(String... args) throws IOException, InterruptedException {
        var capacity = 8;
        try (var selector = Selector.open()) {
            try (var client = SocketChannel.open()) {
                var bind = true;
                if (bind) {
                    client.bind(new InetSocketAddress(HOST, 0));
                    log.debug("[C] bound to {}", client.getLocalAddress());
                }
                client.configureBlocking(false);
                if (client.connect(new InetSocketAddress(HOST, PORT))) {
                    log.debug("[C] connected (immediately) to {}, through {}",
                              client.getRemoteAddress(), client.getLocalAddress());
                    client.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(capacity));
                } else {
                    client.register(selector, SelectionKey.OP_CONNECT);
                }
//                while (!selector.keys().isEmpty()) {
                while (true) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
//                        continue;
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isConnectable()) {
                            var channel = (SocketChannel) key.channel();
                            log.debug("[C] connected to {}, through {}", channel.getRemoteAddress(),
                                      channel.getLocalAddress());
                            key.interestOps(SelectionKey.OP_WRITE);
                            key.attach(ByteBuffer.allocate(capacity));
                            continue;
                        }
                        if (key.isWritable()) {
                            var channel = (SocketChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var written = channel.write(buffer);
                            log.debug("[C] written: {}", written);
                            if (!buffer.hasRemaining()) {
                                key.cancel();
                                break;
                            }
                        }
                    }
                }
                log.debug("[C] closing client...");
            }
            log.debug("[C] closing selector...");
        }
    }

    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
