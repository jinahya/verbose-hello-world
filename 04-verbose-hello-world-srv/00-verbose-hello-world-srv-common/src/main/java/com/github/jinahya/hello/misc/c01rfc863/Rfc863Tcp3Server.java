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

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc863Tcp3Server {

    public static void main(final String... args) throws IOException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // ------------------------------------------------------------------------------- REUSE
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            // -------------------------------------------------------------------------------- BIND
            server.bind(_Rfc863Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------ CONFIGURE/REGISTER
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            // ----------------------------------------------------------------------------- RECEIVE
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS) == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // ---------------------------------------------------------------------- accept
                    if (selectedKey.isAcceptable()) {
                        assert selectedKey == serverKey;
                        final var channel = (ServerSocketChannel) selectedKey.channel();
                        assert channel == server;
                        final var client = _Rfc86_Utils.logAccepted(channel.accept());
                        selectedKey.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                        client.configureBlocking(false);
                        final var clientKey = client.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(new Rfc863Tcp3ServerAttachment(clientKey));
                        continue; // why?
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel != null;
                        final var attachment =
                                (Rfc863Tcp3ServerAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var r = attachment.read();
                        assert r >= -1;
                        assert r != -1 || attachment.isClosed();
                        assert r != -1 || !serverKey.isValid();
                        assert r != -1 || !channel.isOpen();
                    }
                }
            }
        }
    }

    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
