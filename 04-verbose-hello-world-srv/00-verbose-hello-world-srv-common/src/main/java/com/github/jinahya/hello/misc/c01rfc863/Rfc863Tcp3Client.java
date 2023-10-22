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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

@Slf4j
class Rfc863Tcp3Client {

    public static void main(final String... args) throws IOException {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------------------------- BIND
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            // --------------------------------------------------------------------------- CONFIGURE
            client.configureBlocking(false);
            // -------------------------------------------------------------------- CONNECT/REGISTER
            final SelectionKey clientKey;
            if (client.connect(_Rfc863Constants.ADDR)) {
                log.info("(immediately) connected to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_WRITE);
                clientKey.attach(new Rfc863Tcp3ClientAttachment(clientKey));
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            // -------------------------------------------------------------------------------- SEND
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CLIENT_TIMEOUT_MILLIS) == 0) {
                    clientKey.cancel();
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // -------------------------------------------------------------- connect/finish
                    if (selectedKey.isConnectable()) {
                        assert selectedKey == clientKey;
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            _TcpUtils.logConnected(channel);
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.attach(new Rfc863Tcp3ClientAttachment(selectedKey));
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !selectedKey.isWritable(); // @@?
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var attachment =
                                (Rfc863Tcp3ClientAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var w = attachment.write();
//                        assert w >= 0;
//                        assert w > 0 || !attachment.isClosed();
//                        assert w > 0 || !selectedKey.isValid();
                    }
                }
            }
        }
    }

    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
