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

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc862Tcp3Server extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR));
            // ------------------------------------------------------------------ configure/register
            server.configureBlocking(false); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = 0L;
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // ---------------------------------------------------------------------- accept
                    if (key.isAcceptable()) {
                        assert key == serverKey;
                        final var channel = ((ServerSocketChannel) key.channel());
                        assert channel == server;
                        final var client = logAccepted(channel.accept());
                        key.interestOpsAnd(~SelectionKey.OP_ACCEPT); // redundant, why?
                        key.cancel();
                        assert !key.isValid();
                        // ------------------------------------------------------ configure/register
                        client.configureBlocking(false); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        final var clientKey = client.register(
                                selector,             // <sel>
                                SelectionKey.OP_READ, // <ops>
                                newBuffer()           // <att>
                        );
                        continue; // why?
                    }
                    // ------------------------------------------------------------------------ read
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()) {
                            buffer.clear();
                        }
                        final var r = channel.read(buffer);
                        if (r == -1) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                        }
                        assert r >= 0; // why?
                        bytes += r;
                        key.interestOpsOr(SelectionKey.OP_WRITE);
                        assert !key.isWritable();
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        buffer.flip();
                        assert buffer.hasRemaining();
                        final int w = channel.write(buffer);
                        assert w >= 0; // why?
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                        buffer.compact();
                        if (buffer.position() == 0) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            if ((key.interestOps() & SelectionKey.OP_READ) == 0) { // eof
                                channel.close();
                                assert !key.isValid();
                            }
                        }
                    }
                }
            }
            // --------------------------------------------------------------------------------- log
            logServerBytes(bytes);
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
