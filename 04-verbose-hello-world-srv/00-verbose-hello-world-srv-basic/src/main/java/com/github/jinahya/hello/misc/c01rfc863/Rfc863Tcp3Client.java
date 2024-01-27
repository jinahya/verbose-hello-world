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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp3Client extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // ------------------------------------------------------------- connect(try) / register
            final SelectionKey clientKey;
            if (client.connect(ADDR)) {
                logConnected(client);
                clientKey = client.register(selector, SelectionKey.OP_WRITE);
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = logClientBytes(newRandomClientBytes());
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
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
                            assert !key.isWritable();
                            key.attach(newBuffer().limit(0));
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        assert key == clientKey;
                        final var channel = (SocketChannel) key.channel();
                        assert channel == client;
                        final var buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()) {
                            JavaNioByteBufferUtils.randomize(
                                    buffer.clear().limit(Math.min(buffer.limit(), bytes))
                            );
                        }
                        final var w = client.write(buffer);
                        assert w >= 0;
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                        bytes -= w;
                        if (bytes == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE); // redundant
                            key.cancel();
                            assert !key.isValid();
                        }
                    }
                }
            }
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
