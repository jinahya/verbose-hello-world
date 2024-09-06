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
class Rfc863Udp3Server extends Rfc863Udp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR));
            // ------------------------------------------------------------------ configure/register
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_READ);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = ByteBuffer.allocate(
                    server.getOption(StandardSocketOptions.SO_RCVBUF)
            );
            // ------------------------------------------------------------------------------ select
            final var k = selector.select();
            assert k == 1;
            final var key = selector.selectedKeys().iterator().next();
            assert key == serverKey;
            // ----------------------------------------------------------------------------- receive
            assert key.isReadable();
            final var channel = (DatagramChannel) key.channel();
            assert channel == server;
            final var address = channel.receive(buffer);
            assert address != null;
            digest.update(buffer.flip());
            // ------------------------------------------------------------------------------ cancel
            key.cancel();
            assert !key.isValid();
            // --------------------------------------------------------------------------------- log
            logServerBytes(buffer.position());
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Udp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
