package com.github.jinahya.hello.misc.c02rfc862;

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

@Slf4j
class Rfc862Tcp3Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            // ------------------------------------------------------- SERVER/CONFIGURE/NON-BLOCKING
            server.configureBlocking(false);
            // --------------------------------------------------------------------- SERVER/REGISTER
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            // ------------------------------------------------------------------------------ SELECT
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    // --------------------------------------------------------------- server/accept
                    if (selectedKey.isAcceptable()) {
                        assert selectedKey == serverKey;
                        final var channel = ((ServerSocketChannel) selectedKey.channel());
                        assert channel == server;
                        final var client = channel.accept();
                        assert client != null;
                        _Rfc86_Utils.logAccepted(client);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                        // ------------------------------------------- client/configure/non-blocking
                        client.configureBlocking(false);
                        // --------------------------------------------------------- client/register
                        final var clientKey = client.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(new Rfc862Tcp3ServerAttachment(clientKey));
                        continue;
                    }
                    // ----------------------------------------------------------------- client/read
                    if (selectedKey.isReadable()) {
                        final var attachment =
                                (Rfc862Tcp3ServerAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var r = attachment.read();
                        assert r >= -1;
                        assert r != -1 || (selectedKey.interestOps() & SelectionKey.OP_READ) == 0;
                    }
                    // ---------------------------------------------------------------- client/write
                    if (selectedKey.isWritable()) {
                        final var attachment =
                                (Rfc862Tcp3ServerAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var w = attachment.write();
                        assert w >= 0;
                        assert selectedKey.isValid() || !selectedKey.channel().isOpen();
                        assert selectedKey.isValid() || attachment.isClosed();
                    }
                }
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}