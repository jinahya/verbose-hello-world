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
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._UdpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp3Client {

    public static void main(final String... args)
            throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR.getAddress(), 0));
                _UdpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(_Rfc863Constants.ADDR);
                _UdpUtils.logConnected(client);
            }
            // ------------------------------------------------------------------ configure/register
            client.configureBlocking(false);
            final var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select(_Rfc86_Constants.CONNECT_TIMEOUT_MILLIS) == 0) {
                    break;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var selectedKey = i.next();
                    i.remove();
                    // ------------------------------------------------------------------------ send
                    if (selectedKey.isWritable()) {
                        assert selectedKey == clientKey;
                        final var channel = (DatagramChannel) selectedKey.channel();
                        assert channel == client;
                        final var buffer = ByteBuffer.allocate(
                                ThreadLocalRandom.current().nextInt(
                                        channel.getOption(StandardSocketOptions.SO_SNDBUF) + 1
                                )
                        );
                        _Rfc863Utils.logClientBytes(buffer.remaining());
                        if (connect && ThreadLocalRandom.current().nextBoolean()) {
                            final var w = channel.write(buffer);
                            assert w == buffer.position();
                        } else {
                            final var w = channel.send(buffer, _Rfc863Constants.ADDR);
                            assert w == buffer.position();
                        }
                        assert !buffer.hasRemaining();
                        _Rfc863Utils.logDigest(buffer.flip());
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

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
