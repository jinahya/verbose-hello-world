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
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            assert server.isBlocking(); // !!!
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // -------------------------------------------------------------------- accept/configure
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var socket = server.socket().accept();
                assert socket != null;
                client = socket.getChannel();
            } else {
                client = server.accept();
            }
            _TcpUtils.logAccepted(client);
            assert client.isBlocking(); // !!!
            client.socket().setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
            try (client) {
                // ------------------------------------------------------------------------- prepare
                final var digest = _Rfc862Utils.newDigest();
                final var buffer = _Rfc86_Utils.newBuffer();
                assert buffer.hasArray();
                var bytes = 0L;
                for (int r, w; ; bytes += r) {
                    // ------------------------------------------------------------------------ read
                    assert buffer.hasRemaining(); // why?
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
                    assert r >= -1; // -1, 0, 1, 2, ...
                    if (r == -1) {
                        break;
                    }
                    assert r > 0; // why?
                    // ----------------------------------------------------------------------- write
                    buffer.flip(); // limit -> position, position -> zero
                    assert buffer.hasRemaining(); // why?
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        w = buffer.remaining();
                        client.socket().getOutputStream().write(
                                buffer.array(),                           // <b>
                                buffer.arrayOffset() + buffer.position(), // <off>
                                buffer.remaining()                        // <len>
                        );
                        client.socket().getOutputStream().flush();
                        buffer.position(buffer.position() + w);
                    } else {
                        w = client.write(buffer);
                    }
                    assert w > 0; // why?
                    assert !buffer.hasRemaining(); // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    buffer.compact();
                    assert buffer.position() == 0; // why?
                    assert buffer.limit() == buffer.capacity();
                }
                // ------------------------------------------------------------------ write-remained
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    client.write(buffer);
                }
                client.shutdownOutput();
                // ------------------------------------------------------------------------- logging
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
                log.debug("[server] closing client...");
            }
            log.debug("[server] closing server...");
        }
        log.debug("[server] end-of-main");
    }

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
