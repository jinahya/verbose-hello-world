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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

@Slf4j
class Rfc863Tcp2Server {

    // @formatter:on
    static class Attachment extends _Rfc863Attachment.Server {

        /**
         * Reads a sequence of bytes from specified channel into {@link #buffer}.
         *
         * @param channel the channel from which bytes are written.
         * @return a number of bytes read from the {@code channel}.
         * {@link ReadableByteChannel#read(ByteBuffer) channel.read(buffer)}.
         * @throws IOException if an I/O error occurs.
         * @see Rfc863Tcp2Client.Attachment#writeTo(WritableByteChannel)
         */
        int readFrom(final ReadableByteChannel channel) throws IOException {
            if (!buffer.hasRemaining()) {
                buffer.clear();
            }
            final int r = channel.read(buffer);
            if (r <= 0) {
                return r;
            }
            updateDigest(r);
            increaseBytes(r);
            return r;
        }
    }
    // @formatter:on

    public static void main(String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
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
                        var r = attachment.readFrom(channel);
                        if (r == -1) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            channel.close();
                            assert !key.isValid();
                            attachment.logServerBytes();
                            attachment.logDigest();
                            serverKey.cancel();
                            assert !serverKey.isValid();
                        } else {
                            assert r > 0; // why?
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
