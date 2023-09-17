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
            SelectionKey clientKey;
            if (client.connect(_Rfc863Constants.ADDR)) {
                log.info("connected (immediately) to {}, through {}", client.getRemoteAddress(),
                         client.getLocalAddress());
                var attachment = new Rfc863Tcp2ClientAttachment();
                clientKey = client.register(selector, 0, attachment);
                if (attachment.getBytes() == 0) {
                    clientKey.cancel();
                    assert !clientKey.isValid();
                } else {
                    clientKey.interestOpsOr(SelectionKey.OP_WRITE);
                }
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            while (clientKey.isValid()) {
                if (selector.select((int) _Rfc863Constants.CONNECT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isConnectable()) {
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        final var connected = channel.finishConnect();
                        assert connected;
                        log.info("connected to {}, through {}", channel.getRemoteAddress(),
                                 channel.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        var attachment = new Rfc863Tcp2ClientAttachment();
                        key.attach(attachment);
                        if (attachment.getBytes() == 0) {
                            log.warn("no bytes to send");
                            key.cancel();
                            assert !key.isValid();
                            continue;
                        }
                        key.interestOps(SelectionKey.OP_WRITE);
                        continue;
                    }
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        final var attachment = (Rfc863Tcp2ClientAttachment) key.attachment();
                        assert attachment.getBytes() > 0;
                        final var w = attachment.writeTo(channel);
                        assert w > 0; // why?
                        if (attachment.getBytes() == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            attachment.close();
                            key.cancel();
                            assert !key.isValid();
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
