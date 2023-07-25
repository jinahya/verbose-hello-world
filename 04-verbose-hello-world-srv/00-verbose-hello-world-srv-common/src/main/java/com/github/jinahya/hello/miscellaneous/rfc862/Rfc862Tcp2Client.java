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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Tcp2Client {

    private static final InetAddress HOST = Rfc862Tcp2Server.HOST;

    private static final int PORT = Rfc862Tcp2Server.PORT;

    private static final int CAPACITY = Rfc862Tcp2Server.CAPACITY + 2;

    public static void main(String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open()) {
            try (var client = SocketChannel.open()) {
                var bind = true;
                if (bind) {
                    client.bind(new InetSocketAddress(HOST, 0));
                    log.debug("[C] bound to {}", client.getLocalAddress());
                }
                client.configureBlocking(false);
                var connected = client.connect(new InetSocketAddress(HOST, PORT));
                if (connected) {
                    log.debug("connected (immediately) to {}, through {}",
                              client.getRemoteAddress(), client.getLocalAddress());
                    client.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(CAPACITY));
                } else {
                    client.register(selector, SelectionKey.OP_CONNECT,
                                    ByteBuffer.allocate(CAPACITY));
                }
                while (!selector.keys().isEmpty()) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isConnectable()) {
                            var channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            log.debug("[C] connected to {}, through {}", channel.getRemoteAddress(),
                                      channel.getLocalAddress());
                            key.interestOps(SelectionKey.OP_WRITE);
                            continue;
                        }
                        if (key.isReadable()) {
                            var channel = (SocketChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var position = buffer.position();
                            buffer.flip(); // limit -> position, position -> zero
                            var read = channel.read(buffer);
                            log.debug("[C] read: {}", read);
                            buffer.limit(buffer.capacity()).position(position);
                            if (read == -1) {
                                channel.shutdownInput();
                                key.interestOpsAnd(~SelectionKey.OP_READ);
                                assert !buffer.hasRemaining();
                                key.cancel();
                                continue;
                            }
                        }
                        if (key.isWritable()) {
                            var channel = (SocketChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            assert buffer.hasRemaining();
                            var written = channel.write(buffer);
                            log.debug("[C] written: {}", written);
                            if (!buffer.hasRemaining()) {
                                channel.shutdownOutput();
                                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            }
                            if (written > 0) {
                                key.interestOpsOr(SelectionKey.OP_READ);
                            }
                        }
                    }
                }
                assert selector.keys().isEmpty();
            }
        }
    }

    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
