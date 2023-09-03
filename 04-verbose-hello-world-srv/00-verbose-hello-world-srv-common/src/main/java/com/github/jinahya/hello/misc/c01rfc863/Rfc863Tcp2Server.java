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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;

@Slf4j
class Rfc863Tcp2Server {

    // @formatter:off
    static class Attachment {
        /** Creates a new instance. */
        Attachment() {
            super();
            log.debug("buffer.capacity: {}", buffer.capacity());
        }
        /**
         * Updates specified number of bytes preceding current position of {@code buffer} to
         * {@code digest}.
         * @param bytes the number of bytes preceding current position of the {@code buffer} to be
         *              updated to the {@code digest}.
         */
        void updateDigest(int bytes) {
            if (bytes < 0) {
                throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
            }
            digest.update(
                    slice.position(buffer.position() - bytes).limit(buffer.position())
            );
        }
        /**
         * Logs out the final result of {@code digest}.
         * @see _Rfc863Utils#logDigest(MessageDigest)
         */
        void logDigest() {
            _Rfc863Utils.logDigest(digest);
        }
        int bytes;
        final ByteBuffer buffer = _Rfc863Utils.newBuffer();
        final ByteBuffer slice = buffer.slice();
        final MessageDigest digest = _Rfc863Utils.newDigest();
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(ServerSocketChannel.class, server);
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            while (serverKey.isValid()) {
                if (selector.select(_Rfc863Constants.ACCEPT_TIMEOUT_IN_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    var key = i.next();
                    if (key.isAcceptable()) {
                        var channel = (ServerSocketChannel) key.channel();
                        assert channel == server;
                        var client = channel.accept();
                        log.info("accepted from {}, through {}", client.getRemoteAddress(),
                                 client.getLocalAddress());
                        key.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ, new Attachment());
                        continue;
                    }
                    if (key.isReadable()) {
                        var channel = (SocketChannel) key.channel();
                        var attachment = (Attachment) key.attachment();
                        assert attachment != null;
                        if (!attachment.buffer.hasRemaining()) {
                            attachment.buffer.clear();
                        }
                        assert attachment.buffer.hasRemaining();
                        var r = channel.read(attachment.buffer);
                        if (r == -1) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            channel.close();
                            assert !key.isValid();
                            _Rfc863Utils.logServerBytes(attachment.bytes);
                            _Rfc863Utils.logDigest(attachment.digest);
                            serverKey.cancel();
                            assert !serverKey.isValid();
                        } else {
                            assert r > 0;
                            attachment.bytes += r;
                            attachment.digest.update(
                                    attachment.slice
                                            .position(attachment.buffer.position() - r)
                                            .limit(attachment.buffer.position())
                            );
                        }
                    }
                }
            }
        }
    }

    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
