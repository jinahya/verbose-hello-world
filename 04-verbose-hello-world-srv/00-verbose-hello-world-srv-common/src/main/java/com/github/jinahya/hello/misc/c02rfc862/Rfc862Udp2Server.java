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
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._UdpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class Rfc862Udp2Server {

    public static void main(final String... args) throws Exception {
        // ------------------------------------------------------------------------------------ open
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR);
            _UdpUtils.logBound(server);
            // ------------------------------------------------------------------ configure/register
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_READ);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            final var buffer = ByteBuffer.allocate(
                    server.getOption(StandardSocketOptions.SO_RCVBUF)
            );
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // --------------------------------------------------------------------- receive
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == server;
                        final var address = channel.receive(buffer);
                        assert address != null;
                        selectedKey.attach(address);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        buffer.flip();
                        selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                    }
                    // ------------------------------------------------------------------------ send
                    if (selectedKey.isWritable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == server;
                        final var attachment = (SocketAddress) selectedKey.attachment();
                        assert attachment != null;
                        final int w = channel.send(buffer, attachment);
                        assert w >= 0;
                        assert !buffer.hasRemaining();
                        JavaSecurityUtils.updateDigest(digest, buffer, w);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                    }
                }
            }
            _Rfc862Utils.logDigest(digest);
        }
    }

    private Rfc862Udp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
