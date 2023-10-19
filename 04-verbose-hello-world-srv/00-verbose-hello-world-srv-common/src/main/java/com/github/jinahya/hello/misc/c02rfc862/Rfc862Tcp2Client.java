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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Client {

    public static void main(final String... args) throws Exception {
        try (var client = SocketChannel.open()) {
            assert client.isBlocking();
            // -------------------------------------------------------------------------------- BIND
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            // ----------------------------------------------------------------------------- CONNECT
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().connect(
                        _Rfc862Constants.ADDR,                           // <endpoint>
                        (int) _Rfc86_Constants.CONNECT_TIMEOUT_IN_MILLIS // <timeout>
                );
                _Rfc86_Utils.logConnected(client.socket());
            } else {
                final var connected = client.connect(_Rfc862Constants.ADDR);
                assert connected || !client.isBlocking();
                _Rfc86_Utils.logConnected(client);
            }
            assert client.isConnected();
            assert client.socket().isConnected();
            // ------------------------------------------------------------------------ SEND/RECEIVE
            final var digest = _Rfc862Utils.newDigest();
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.hasArray();
            final var slice = buffer.slice();
            assert slice.hasArray();
            assert slice.array() == buffer.array();
            var bytes = _Rfc862Utils.logClientBytes(_Rfc86_Utils.randomBytes());
            int w; // number of bytes written
            int r; // number of bytes read
            while (bytes > 0) {
                // --------------------------------------------------------------------------- write
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                assert buffer.hasRemaining();
                if (ThreadLocalRandom.current().nextBoolean()) {
                    w = buffer.remaining(); // @@?
                    client.socket().getOutputStream().write(
                            buffer.array(),                           // <b>
                            buffer.arrayOffset() + buffer.position(), // <off>
                            buffer.remaining()                        // <len>
                    );
                    client.socket().getOutputStream().flush();
                    buffer.position(buffer.position() + w); // buffer.position(buffer.limit())
                } else {
                    w = client.write(buffer);
                }
                assert w > 0; // why?
                assert !buffer.hasRemaining(); // why?
                bytes -= w;
                // ------------------------------------------------------------------- update digest
                digest.update(
                        slice.position(buffer.position() - w)
                                .limit(buffer.position())
                );
                // ---------------------------------------------------------------------------- read
                final var limit = buffer.limit();
                buffer.flip(); // limit -> position, position -> zero
                assert buffer.remaining() == w;
                if (ThreadLocalRandom.current().nextBoolean()) {
                    r = client.socket().getInputStream().read(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.remaining()
                    );
                    if (r != -1) {
                        buffer.position(buffer.position() + r);
                    }
                } else {
                    r = client.read(buffer);
                }
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
                assert r > 0; // why?
                buffer.position(buffer.limit()).limit(limit);
            }
            // --------------------------------------------------------------------- shutdown output
            client.shutdownOutput();
            // --------------------------------------------------------------------- read to the eof
            buffer.clear();
            while (true) {
                r = client.read(buffer);
                if (r == -1) {
                    break;
                }
                assert r >= 0;
                buffer.clear();
            }
            _Rfc862Utils.logDigest(digest);
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
