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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Udp3Client extends _Rfc863Udp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var client = DatagramChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ------------------------------------------------------------------- connect(optional)
            final var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                client.connect(ADDR);
                logConnected(client);
            }
            // ---------------------------------------------------------------- configure / register
            client.configureBlocking(false);
            final var clientKey = client.register(selector, SelectionKey.OP_WRITE);
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // ------------------------------------------------------------------------ send
                    if (key.isWritable()) {
                        assert key == clientKey;
                        final var channel = (DatagramChannel) key.channel();
                        assert channel == client;
                        final var buffer = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(
                                (channel.getOption(StandardSocketOptions.SO_SNDBUF) >> 1) + 1
                        ));
                        ThreadLocalRandom.current().nextBytes(buffer.array());
                        logClientBytes(buffer.remaining());
                        if (connect) {
                            final var w = channel.write(buffer);
                            assert w == buffer.position();
                        } else {
                            final var w = channel.send(buffer, ADDR);
                            assert w == buffer.position();
                        }
                        assert !buffer.hasRemaining();
                        logDigest(buffer.flip());
                        key.cancel();
                        assert !key.isValid();
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