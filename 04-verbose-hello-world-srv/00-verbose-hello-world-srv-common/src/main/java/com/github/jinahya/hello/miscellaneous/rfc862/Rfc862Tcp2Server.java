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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Server {

    static final InetAddress HOST = Rfc862Tcp1Server.HOST;

    static final int PORT = Rfc862Tcp1Server.PORT;

    static final int CAPACITY = 6;

    public static void main(String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open()) {
            try (var server = ServerSocketChannel.open()) {
                server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
                server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
                server.bind(new InetSocketAddress(HOST, PORT));
                log.debug("[S] server bound to {}", server.getLocalAddress());
                server.socket().setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
                server.configureBlocking(false);
                server.register(selector, SelectionKey.OP_ACCEPT);
                while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isAcceptable()) {
                            var client = ((ServerSocketChannel) key.channel()).accept();
                            log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                                      client.getLocalAddress());
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ,
                                            ByteBuffer.allocate(CAPACITY));
                            key.cancel();
                            continue;
                        }
                        if (key.isReadable()) {
                            var channel = (SocketChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var read = channel.read(buffer);
                            log.debug("[S] read: {}", read);
                            if (read == -1) {
                                channel.shutdownInput();
                                key.interestOpsAnd(~SelectionKey.OP_READ);
                            }
                            if (read > 0) {
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                            }
                        }
                        if (key.isWritable()) {
                            var channel = (SocketChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            buffer.flip(); // limit -> position; position -> zero
                            assert buffer.hasRemaining();
                            var written = channel.write(buffer);
                            log.debug("[S] written: {}", written);
                            buffer.compact();
                            if (buffer.position() == 0) {
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                                if ((key.interestOps() & SelectionKey.OP_READ) == 0) {
                                    channel.shutdownOutput();
                                    key.cancel();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
