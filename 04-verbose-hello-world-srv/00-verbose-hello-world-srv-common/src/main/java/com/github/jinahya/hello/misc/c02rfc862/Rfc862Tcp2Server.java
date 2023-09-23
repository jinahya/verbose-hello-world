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
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc862Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    if (key.isAcceptable()) {
                        final var channel = ((ServerSocketChannel) key.channel());
                        assert channel == server;
                        final var client = channel.accept();
                        log.info("accepted from {}, through {}", client.getRemoteAddress(),
                                 client.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        key.cancel();
                        assert !key.isValid();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ,
                                        new Rfc862Tcp2ServerAttachment());
//                        continue;
                    }
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var attachment = (Rfc862Tcp2ServerAttachment) key.attachment();
                        assert attachment != null;
                        final var r = attachment.readFrom(channel);
                        assert r >= -1;
                        if (r == -1) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                        }
                        if (r > 0) {
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    }
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var attachment = (Rfc862Tcp2ServerAttachment) key.attachment();
                        assert attachment != null;
                        final var w = attachment.writeTo(channel);
                        assert w >= 0;
                        if (w == 0) {
                            if ((key.interestOps() & SelectionKey.OP_READ)
                                == SelectionKey.OP_READ) {
                                key.interestOpsOr(~SelectionKey.OP_WRITE);
                            } else {
                                key.cancel();
                                assert !key.isValid();
                            }
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
