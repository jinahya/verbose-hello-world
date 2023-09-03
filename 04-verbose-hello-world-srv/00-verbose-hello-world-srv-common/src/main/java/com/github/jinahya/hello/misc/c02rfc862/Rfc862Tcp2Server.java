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

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;

@Slf4j
class Rfc862Tcp2Server {

    // @formatter:off
    static class Attachment {
        Attachment() {
            super();
            log.debug("buffer.capacity: {}", buffer.capacity());
        }
        int bytes;
        final ByteBuffer buffer = _Rfc862Utils.newBuffer();
        final ByteBuffer slice = buffer.slice();
        final MessageDigest digest = _Rfc862Utils.newDigest();
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc862Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isAcceptable()) {
                        var channel = ((ServerSocketChannel) key.channel());
                        assert channel == server;
                        var client = channel.accept();
                        log.info("accepted from {}, through {}", client.getRemoteAddress(),
                                 client.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        key.cancel();
                        assert !key.isValid();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ, new Attachment());
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            if (attachment.buffer.position() == 0) {
                                channel.close();
                                assert !key.isValid();
                                _Rfc862Utils.logServerBytes(attachment.bytes);
                                _Rfc862Utils.logDigest(attachment.digest);
                                continue;
                            }
                        } else {
                            attachment.bytes += r;
                        }
                        if (attachment.buffer.position() > 0) {
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                        }
                    }
                    if (key.isWritable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        assert attachment.buffer.position() > 0;
                        attachment.buffer.flip();
                        assert attachment.buffer.hasRemaining();
                        var w = channel.write(attachment.buffer);
                        assert w > 0;
                        attachment.digest.update(
                                attachment.slice
                                        .position(attachment.buffer.position() - w)
                                        .limit(attachment.buffer.position())
                        );
                        if (attachment.buffer.compact().position() == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
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
