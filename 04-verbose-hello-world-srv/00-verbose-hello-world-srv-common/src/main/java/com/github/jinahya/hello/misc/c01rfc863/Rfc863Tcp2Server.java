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

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc863Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            assert server.isBlocking();
            // ------------------------------------------------------------------------------- REUSE
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            // -------------------------------------------------------------------------------- BIND
            server.bind(_Rfc863Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------------------ ACCEPT
            final SocketChannel client;
            server.socket().setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS);
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var socket = server.socket().accept();
                assert socket != null;
                client = socket.getChannel();
            } else {
                client = server.accept();
            }
            assert client != null;
            _Rfc86_Utils.logAccepted(client);
            assert client.isBlocking();
            // ----------------------------------------------------------------------------- RECEIVE
            try (client) {
                client.socket().setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
                final var digest = _Rfc863Utils.newDigest();
                int bytes = 0;
                final var buffer = _Rfc86_Utils.newBuffer();
                assert buffer.hasArray();
                final var slice = buffer.slice();
                assert slice.hasArray();
                assert slice.array() == buffer.array();
                for (int r; ; bytes += r) {
                    // ------------------------------------------------------------------------ read
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    assert buffer.hasRemaining();
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        r = client.socket().getInputStream().read(
                                buffer.array(),                           // <b>
                                buffer.arrayOffset() + buffer.position(), // <off>
                                buffer.remaining()                        // <len>
                        );
                        if (r != -1) {
                            buffer.position(buffer.position() + r);
                        }
                    } else {
                        r = client.read(buffer);
                    }
                    if (r == -1) {
                        break;
                    }
                    // ---------------------------------------------------------------------- digest
                    digest.update(
                            slice.position(buffer.position() - r)
                                    .limit(buffer.position())
                    );
                }
                _Rfc863Utils.logServerBytes(bytes);
                _Rfc863Utils.logDigest(digest);
            }
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
