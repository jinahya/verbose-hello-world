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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Udp2Client {

    public static void main(final String... args)
            throws IOException {
        // ------------------------------------------------------------------------------------ open
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR.getAddress(), 0));
                _UdpUtils.logBound(client);
            }
            // ------------------------------------------------------------------- connect(optional)
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc862Constants.ADDR);
                _UdpUtils.logConnected(client);
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            final var buffer = ByteBuffer.allocate(
                    ThreadLocalRandom.current().nextInt(
//                            client.getOption(StandardSocketOptions.SO_SNDBUF) + 1
                            (client.getOption(StandardSocketOptions.SO_SNDBUF) >> 1) + 1
                    )
            );
            ThreadLocalRandom.current().nextBytes(buffer.array());
            _Rfc862Utils.logClientBytes(buffer.remaining());
            // ------------------------------------------------------------------ configure/register
            client.configureBlocking(false);
            final var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            // ---------------------------------------------------------------------- select-in-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    assert selectedKey == clientKey;
                    // ------------------------------------------------------------------------ send
                    if (selectedKey.isWritable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        final var remaining = buffer.remaining();
                        assert channel == client;
                        final int w;
                        if (channel.isConnected()) {
                            w = channel.write(buffer);
                        } else {
                            w = channel.send(buffer, _Rfc862Constants.ADDR);
                        }
                        assert w == remaining;
                        assert !buffer.hasRemaining();
                        JavaSecurityUtils.updateDigest(digest, buffer, w);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                        selectedKey.interestOpsOr(SelectionKey.OP_READ);
                    }
                    // --------------------------------------------------------------------- receive
                    if (selectedKey.isReadable()) {
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == client;
                        buffer.clear();
                        final SocketAddress address;
                        final int r;
                        if (channel.isConnected()) {
                            r = channel.read(buffer);
                        } else {
                            address = channel.receive(buffer);
                            r = buffer.position();
                        }
                        if (r == -1 || buffer.hasRemaining()) {
                            throw new IllegalArgumentException("unexpected eof");
                        }
                        _Rfc862Utils.logDigest(digest);
                        selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                        selectedKey.cancel();
                        assert !selectedKey.isValid();
                    }
                }
            }
            // -------------------------------------------------------------------------- disconnect
            if (connect) {
                client.disconnect();
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
