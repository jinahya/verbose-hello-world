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

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class Rfc863Udp3Server extends _Rfc863Udp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR);
            logBound(server);
            // ---------------------------------------------------------------- configure / register
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_READ);
            // ------------------------------------------------------------------------------ select
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                if (selector.select() == 0) {
                    continue;
                }
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // --------------------------------------------------------------------- receive
                    if (key.isReadable()) {
                        final var channel = (DatagramChannel) key.channel();
                        assert channel == server;
                        assert key == serverKey;
                        final var buffer = ByteBuffer.allocate(
                                channel.getOption(StandardSocketOptions.SO_RCVBUF)
                        );
                        final var address = channel.receive(buffer);
                        assert address != null;
                        logServerBytes(buffer, address);
                        logDigest(buffer.flip());
                        key.cancel();
                        assert !key.isValid();
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
