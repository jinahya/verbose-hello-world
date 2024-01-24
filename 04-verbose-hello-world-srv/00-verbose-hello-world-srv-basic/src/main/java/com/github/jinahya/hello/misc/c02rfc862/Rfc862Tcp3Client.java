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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp3Client extends _Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ------------------------------------------------------------------------ connect(try)
            final SelectionKey clientKey;
            if (client.connect(ADDR)) {
                assert client.isConnected();
                logConnected(client);
                clientKey = client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        JavaNioByteBufferUtils.randomize(newBuffer())
                );
            } else {
                clientKey = client.register(
                        selector,
                        SelectionKey.OP_CONNECT
                );
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = logClientBytes(newRandomBytes());
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // ------------------------------------------------------------- connect(finish)
                    if (key.isConnectable()) {
                        assert key == clientKey;
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            logConnected(channel);
                            key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                            key.attach(JavaNioByteBufferUtils.randomize(newBuffer()));
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        assert bytes > 0;
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        final var buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()) {
                            JavaNioByteBufferUtils.randomize(
                                    buffer.clear().limit(Math.min(buffer.limit(), bytes))
                            );
                        }
                        final var w = channel.write(buffer);
                        assert w >= 0;
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                        bytes -= w;
                        if (bytes == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            logDigest(digest);
                            channel.shutdownOutput();
                        }
                        if (buffer.position() > 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            buffer.flip();
                            key.interestOpsOr(SelectionKey.OP_READ);
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        final var buffer = (ByteBuffer) key.attachment();
                        assert buffer.hasRemaining();
                        final int r = channel.read(buffer);
                        if (r == -1) {
                            throw new EOFException("unexpected eof");
                        }
                        assert r >= 0;
                        if (!buffer.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            if (bytes > 0) {
                                buffer.position(buffer.limit()).limit(buffer.capacity());
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                            } else {
                                channel.close();
                                assert !key.isValid();
                            }
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
