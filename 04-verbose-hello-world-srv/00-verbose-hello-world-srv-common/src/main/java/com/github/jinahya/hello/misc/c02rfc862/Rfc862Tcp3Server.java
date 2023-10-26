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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
class Rfc862Tcp3Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------ configure/register
            server.configureBlocking(false);
            final var serverKey = server.register(
                    selector,              // <sel>
                    SelectionKey.OP_ACCEPT // <ops>
            );
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            assert bytes >= 0;
            final var buffer = _Rfc86_Utils.newBuffer();
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // ---------------------------------------------------------------------- accept
                    if (selectedKey.isAcceptable()) {
                        assert selectedKey == serverKey;
                        final var channel = ((ServerSocketChannel) selectedKey.channel());
                        assert channel == server;
                        final var client = channel.accept();
                        _TcpUtils.logAccepted(client);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_ACCEPT);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                        // ------------------------------------------------------ configure/register
                        client.configureBlocking(false);
                        final var clientKey = client.register(
                                selector,            // <sel>
                                SelectionKey.OP_READ // <ops>
                        );
                        continue; // why?
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        if (!buffer.hasRemaining()) {
                            buffer.clear();
                        }
                        final var r = channel.read(buffer);
                        assert r >= -1;
                        if (r == -1) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        }
                        bytes += r;
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        buffer.flip();
                        final int w = channel.write(buffer);
                        assert w >= 0;
                        JavaSecurityUtils.updateDigest(digest, buffer, w);
                        buffer.compact();
                        if (buffer.position() == 0) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            if ((selectedKey.interestOps() & SelectionKey.OP_READ) == 0) {
                                channel.close();
                                log.debug("channel closed");
                                assert !selectedKey.isValid();
                                continue;
                            }
                        }
                    }
                }
            }
            _Rfc862Utils.logServerBytes(bytes);
            _Rfc862Utils.logDigest(digest);
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
