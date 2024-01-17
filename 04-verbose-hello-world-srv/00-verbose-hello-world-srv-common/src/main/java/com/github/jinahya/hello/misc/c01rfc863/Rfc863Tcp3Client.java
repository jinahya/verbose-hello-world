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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp3Client {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = SocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // -------------------------------------------------------------- configure non-blocking
            client.configureBlocking(false);
            // --------------------------------------------------------------- connect(try)/register
            final SelectionKey clientKey;
            if (client.connect(_Rfc863Constants.ADDR)) {
                _TcpUtils.logConnected(client);
                clientKey = client.register(selector, SelectionKey.OP_WRITE);
            } else {
                clientKey = client.register(selector, SelectionKey.OP_CONNECT);
            }
            // ----------------------------------------------------------------------------- prepare
            var bytes = _Rfc863Utils.logClientBytes(_Rfc86_Utils.newRandomBytes());
            final var digest = _Rfc863Utils.newDigest();
            final var buffer = _Rfc86_Utils.newBuffer();
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(Math.min(buffer.limit(), bytes));
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // ------------------------------------------------------------- connect(finish)
                    if (selectedKey.isConnectable()) {
                        assert selectedKey == clientKey;
                        final var channel = (SocketChannel) selectedKey.channel();
                        assert channel == client;
                        if (channel.finishConnect()) {
                            _TcpUtils.logConnected(channel);
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !selectedKey.isWritable(); // @@?
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        if (!buffer.hasRemaining()) {
                            ThreadLocalRandom.current().nextBytes(buffer.array());
                            buffer.clear().limit(Math.min(buffer.limit(), bytes));
                        }
                        final var w = client.write(buffer);
                        assert w >= 0;
                        JavaSecurityUtils.updateDigest(digest, buffer, w);
                        if ((bytes -= w) == 0) {
                            _Rfc863Utils.logDigest(digest);
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            selectedKey.cancel();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
