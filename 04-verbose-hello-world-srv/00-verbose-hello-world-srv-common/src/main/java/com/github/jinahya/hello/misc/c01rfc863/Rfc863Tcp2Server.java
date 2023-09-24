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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc863Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
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
                        final var clientKey = client.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(new Rfc863Tcp2ServerAttachment(clientKey));
                        continue;
                    }
                    if (selectedKey.isReadable()) {
                        assert selectedKey.channel() instanceof SocketChannel;
                        final var attachment =
                                (Rfc863Tcp2ServerAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var r = attachment.read();
                        assert r >= -1;
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
