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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings({"java:S101"})
class Z_Rfc863Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc863Constants.ADDR, Z__Rfc863Constants.SERVER_BACKLOG);
            log.info("bound to {}", server.getLocalAddress());
            JavaLangUtils.readLinesAndCloseWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    server,                        // <closeable>
                    null                           // <consumer>
            );
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(TimeUnit.SECONDS.toMillis(1L)) == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isAcceptable()) {
                        final var channel = (ServerSocketChannel) serverKey.channel();
                        assert channel == server;
                        final var client = channel.accept();
                        client.configureBlocking(false);
                        final var clientKey = client.register(
                                selector,
                                SelectionKey.OP_READ,
                                ByteBuffer.allocate(Z__Rfc863Constants.SERVER_BUFLEN)
                        );
                    }
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var attachment = (ByteBuffer) selectedKey.attachment();
                        if (!attachment.hasRemaining()) {
                            attachment.clear();
                        }
                        final var r = channel.read(attachment);
                        if (r == -1) {
                            channel.close();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new instance.
     */
    private Z_Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
