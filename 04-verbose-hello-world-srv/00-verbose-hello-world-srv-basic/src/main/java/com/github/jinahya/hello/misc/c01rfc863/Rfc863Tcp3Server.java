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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc863Tcp3Server extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // ----------------------------------------------------- configure-non-blocking/register
            final var serverKey = server.configureBlocking(false) // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    .register(selector, SelectionKey.OP_ACCEPT);
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 1));
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = 0L;
            // ---------------------------------------------------------------------- select/process
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    // ---------------------------------------------------------------------- accept
                    if (selectedKey.isAcceptable()) {
                        assert selectedKey == serverKey;
                        final var channel = (ServerSocketChannel) selectedKey.channel();
                        assert channel == server;
                        final var client = logAccepted(channel.accept());
                        selectedKey.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                        // ----------------------------------------- configure-non-blocking/register
                        final var clientKey = client.configureBlocking(false).register(
                                selector,             // <sel>
                                SelectionKey.OP_READ, // <ops>
                                newBuffer()           // <att>
                        );
                        assert !clientKey.isReadable();
                        continue; // why?
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var buffer = (ByteBuffer) selectedKey.attachment();
                        if (!buffer.hasRemaining()) {
                            buffer.clear();
                        }
                        final var r = channel.read(buffer);
                        if (r == -1) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ); // redundant
                            channel.shutdownInput(); // redundant
                            channel.close();
                            assert !selectedKey.isValid();
                            continue;
                        }
                        assert r > 0; // why?
                        bytes += r;
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, r);
                    }
                }
            }
            // --------------------------------------------------------------------------------- log
            logServerBytes(bytes);
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
