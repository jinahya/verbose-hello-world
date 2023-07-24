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
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
class Rfc862Tcp2Server {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = 7 + 52000;

    public static void main(String... args) throws IOException, InterruptedException {
        try (var sel = Selector.open()) {
            try (var server = ServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(HOST, PORT));
                log.info("[S] server bound to {}", server.getLocalAddress());
                server.socket().setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
                server.configureBlocking(false);
                server.register(sel, SelectionKey.OP_ACCEPT);
                while (!sel.keys().isEmpty()) {
                    if (sel.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        break;
//                        continue;
                    }
                    for (var i = sel.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isAcceptable()) {
                            var client = ((ServerSocketChannel) key.channel()).accept();
                            log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                                      client.getLocalAddress());
                            client.configureBlocking(false);
                            client.register(
                                    sel,
                                    SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                                    ByteBuffer.allocate(1)
                            );
                            key.cancel();
                            continue;
                        }
                        if (key.isReadable()) {
                            var channel = (ReadableByteChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            var read = channel.read(buffer);
                            if (read == -1) {
                                key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
                            }
                            log.debug("[S] read: {}", read);
                        }
                        if (key.isWritable()) {
                            var channel = (WritableByteChannel) key.channel();
                            var buffer = (ByteBuffer) key.attachment();
                            buffer.flip(); // limit -> position; position -> zero
                            var written = channel.write(buffer);
                            log.debug("written: {}", written);
                            buffer.compact();
                            if (buffer.position() == 0
                                && (key.interestOps() & SelectionKey.OP_READ)
                                   != SelectionKey.OP_READ) {
                                key.cancel();
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
