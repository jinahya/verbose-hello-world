package com.github.jinahya.hello.misc.c01rfc863.real;

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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc863TcpNonBlockingServer {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            JavaLangUtils.readLinesAndRunWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    serverKey::cancel,             // <callable>
                    null                           // <consumer>
            );
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.SERVER_TIMEOUT) == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isAcceptable()) {
                        assert selectedKey == serverKey;
                        final var channel = (ServerSocketChannel) selectedKey.channel();
                        assert channel == server;
                        final var client = channel.accept();
                        _Rfc86_Utils.logAccepted(client);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_ACCEPT); // redundant
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                        client.configureBlocking(false);
                        final var clientKey = client.register(
                                selector,
                                SelectionKey.OP_READ,
                                ByteBuffer.allocate(_Rfc863Constants.SERVER_BUFLEN)
                        );
                        continue;
                    }
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var attachment = (ByteBuffer) selectedKey.attachment();
                        assert attachment != null;
                        if (!attachment.hasRemaining()) {
                            attachment.clear();
                        }
                        if (channel.read(attachment) == -1) {
                            channel.close();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
        }
    }

    private Rfc863TcpNonBlockingServer() {
        throw new AssertionError("instantiation is not allowed");
    }
}
