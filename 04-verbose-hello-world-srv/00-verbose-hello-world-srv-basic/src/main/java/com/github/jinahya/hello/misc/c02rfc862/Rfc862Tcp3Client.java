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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp3Client extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // --------------------------------------------------------------- connect(try)/register
            final SelectionKey clientKey;
            if (client.connect(ADDR)) {
                clientKey = logConnected(client).register(
                        selector,              // <sel>
                        SelectionKey.OP_WRITE, // <ops>
                        newBuffer().limit(0)   // <att>
                );
            } else {
                clientKey = client.register(
                        selector,               // <sel>
                        SelectionKey.OP_CONNECT // <ops>
                );
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = logClientBytes(newRandomBytes());
            // ---------------------------------------------------------------------- select/process
            while (clientKey.isValid()) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                final var selectedKeys = selector.selectedKeys();
                final var selectedKey = selectedKeys.iterator().next();
                assert selectedKey == clientKey;
                selectedKeys.clear();
                // ----------------------------------------------------------------- connect(finish)
                if (selectedKey.isConnectable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    assert channel == client;
                    if (channel.finishConnect()) {
                        logConnected(channel);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                        selectedKey.attach(newBuffer().limit(0));
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                        assert !selectedKey.isWritable();
                    }
                }
                // --------------------------------------------------------------------------- write
                if (selectedKey.isWritable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    assert channel == client;
                    final var buffer = (ByteBuffer) selectedKey.attachment();
                    if (!buffer.hasRemaining()) {
                        ThreadLocalRandom.current().nextBytes(buffer.array());
                        buffer.clear().limit(Math.min(buffer.limit(), bytes));
                    }
                    assert buffer.hasRemaining() || bytes == 0;
                    final var w = channel.write(buffer);
                    assert w >= 0; // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    bytes -= w;
                    if (bytes == 0) {
                        // --------------------------------------------------------- shutdown-output
                        channel.shutdownOutput();
                        // --------------------------------------------------------------------- log
                        logDigest(digest);
                    }
                    selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                    buffer.flip();
                    assert buffer.hasRemaining(); // why?
                    selectedKey.interestOpsOr(SelectionKey.OP_READ);
                    assert !selectedKey.isReadable();
                }
                // ---------------------------------------------------------------------------- read
                if (selectedKey.isReadable()) {
                    final var channel = (SocketChannel) selectedKey.channel();
                    assert channel == client;
                    final var buffer = (ByteBuffer) selectedKey.attachment();
                    assert buffer.hasRemaining(); // why?
                    final int r = channel.read(buffer);
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    assert r >= 0; // why?
                    if (!buffer.hasRemaining()) {
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        if (bytes == 0) {
                            channel.close();
                            assert !selectedKey.isValid();
                            return;
                        }
                        buffer.limit(Math.min(buffer.limit(), bytes));
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                        assert !selectedKey.isWritable();
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
