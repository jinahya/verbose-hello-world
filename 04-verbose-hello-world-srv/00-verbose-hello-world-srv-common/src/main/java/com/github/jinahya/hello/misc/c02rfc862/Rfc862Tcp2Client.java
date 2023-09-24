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

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Client {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            final SelectionKey clientKey;
            if (client.connect(_Rfc862Constants.ADDR)) {
                log.info("(immediately) connected to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_WRITE);
                clientKey.attach(new Rfc862Tcp2ClientAttachment(clientKey));
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CONNECT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isConnectable()) {
                        assert selectedKey == clientKey;
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            _Rfc86_Utils.logConnected(channel);
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            assert selectedKey.attachment() == null;
                            selectedKey.attach(new Rfc862Tcp2ClientAttachment(selectedKey));
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    }
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        final var attachment =
                                (Rfc862Tcp2ClientAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var w = attachment.write();
                        assert w > 0;
                    }
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        final var attachment =
                                (Rfc862Tcp2ClientAttachment) selectedKey.attachment();
                        assert attachment != null;
                        final var r = attachment.read();
                        assert r >= -1;
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
