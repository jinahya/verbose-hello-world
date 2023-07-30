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

import com.github.jinahya.hello.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863Tcp2Server {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = 9 + 51000;

    static final int CAPACITY = 1024;

    static final String ALGORITHM = "SHA-1";

    static class Attachment {

        ByteBuffer buffer;

        int bytes;

        MessageDigest digest;
    }

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open()) {
            try (var server = ServerSocketChannel.open()) {
                server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
                server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
                server.bind(new InetSocketAddress(HOST, PORT), 1);
                log.debug("[S] bound to {}", server.getLocalAddress());
                server.configureBlocking(false);
                server.register(selector, SelectionKey.OP_ACCEPT);
                while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                    if (selector.select(TimeUnit.SECONDS.toMillis(8L)) == 0) {
                        continue;
                    }
                    for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                        var key = i.next();
                        if (key.isAcceptable()) {
                            var channel = (ServerSocketChannel) key.channel();
                            var client = channel.accept();
                            log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                                      client.getLocalAddress());
                            var attachment = new Attachment();
                            attachment.buffer = ByteBuffer.allocate(CAPACITY);
                            attachment.digest = MessageDigest.getInstance(ALGORITHM);
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ, attachment);
                            key.cancel();
                            continue;
                        }
                        if (key.isReadable()) {
                            var channel = (SocketChannel) key.channel();
                            var attachment = (Attachment) key.attachment();
                            if (!attachment.buffer.hasRemaining()) {
                                attachment.buffer.clear();
                            }
                            var read = channel.read(attachment.buffer);
                            log.trace("[S] - read: {}", read);
                            if (read == -1) {
                                log.debug("[S] byte(s) received (and discarded): {}",
                                          attachment.bytes);
                                log.debug("[S] digest: {}",
                                          HexFormat.of().formatHex(attachment.digest.digest()));
                                channel.close();
                                key.cancel();
                                continue;
                            }
                            attachment.bytes += read;
                            HelloWorldServerUtils.updatePreceding(
                                    attachment.digest,
                                    attachment.buffer,
                                    read
                            );
                        }
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
