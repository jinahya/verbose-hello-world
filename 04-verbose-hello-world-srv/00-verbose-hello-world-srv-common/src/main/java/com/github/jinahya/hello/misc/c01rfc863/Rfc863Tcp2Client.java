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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.misc.c01rfc863._Rfc863Constants.HOST;

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Rfc863Tcp2Server
 */
@Slf4j
class Rfc863Tcp2Client {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            client.configureBlocking(false);
            final SelectionKey clientKey;
            if (client.connect(_Rfc863Constants.ADDR)) {
                log.info("(immediately) connected to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                clientKey = client.register(selector, SelectionKey.OP_WRITE,
                                            new Rfc863Tcp2ClientAttachment());
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CLIENT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    if (selectedKey.isConnectable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            log.info("connected to {}, through {}", channel.getRemoteAddress(),
                                     channel.getLocalAddress());
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.attach(new Rfc863Tcp2ClientAttachment());
                            selectedKey.interestOps(SelectionKey.OP_WRITE);
                        }
                    }
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        final var attachment = (Rfc863Tcp2ClientAttachment) selectedKey.attachment();
                        final var w = attachment.write(channel);
                        assert w >= 0; // why?
                        if (attachment.getBytes() == 0) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            attachment.close();
                            selectedKey.cancel();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
