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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp3Client {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // ---------------------------------------------------------------------- try-to-connect
            final SelectionKey clientKey;
            assert !client.isConnected();
            if (client.connect(_Rfc862Constants.ADDR)) {
                assert client.isConnected();
                _TcpUtils.logConnected(client);
                clientKey = client.register(
                        selector,                                    // <sel>
                        SelectionKey.OP_WRITE | SelectionKey.OP_READ // <ops>
                );
            } else {
                clientKey = client.register(
                        selector,               // <sel>
                        SelectionKey.OP_CONNECT // <ops>
                );
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            assert bytes >= 0;
            _Rfc862Utils.logClientBytes(bytes);
            final var buffer = _Rfc86_Utils.newBuffer();
            buffer.position(buffer.limit()); // for what?
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // ----------------------------------------------------------- finish-connecting
                    if (selectedKey.isConnectable()) {
                        assert selectedKey == clientKey;
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            _TcpUtils.logConnected(channel);
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (!buffer.hasRemaining()) {
                            ThreadLocalRandom.current().nextBytes(buffer.array());
                            buffer.clear().limit(Math.min(buffer.limit(), bytes));
                        }
                        assert buffer.hasRemaining() || bytes == 0;
                        final var w = channel.write(buffer);
                        JavaSecurityUtils.updateDigest(digest, buffer, w);
                        bytes -= w;
                        if (bytes == 0) {
                            _Rfc862Utils.logDigest(digest);
                            channel.shutdownOutput();
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        final var limit = buffer.limit();
                        buffer.flip(); // limit -> position, position -> zero;
                        final int r = channel.read(buffer);
                        assert r >= -1;
                        if (r == -1) {
                            if (bytes > 0) {
                                throw new EOFException("unexpected eof");
                            }
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            selectedKey.cancel();
                            assert !selectedKey.isValid();
                            continue;
                        }
                        buffer.position(buffer.limit()).limit(limit);
                    }
                }
            }
        }
    }

    private Rfc862Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
