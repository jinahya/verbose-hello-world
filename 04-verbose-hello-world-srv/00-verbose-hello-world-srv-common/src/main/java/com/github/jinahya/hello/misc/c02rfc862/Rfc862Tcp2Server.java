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

import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            assert server.isBlocking();
            // -------------------------------------------------------------------------------- BIND
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            // ------------------------------------------------------------------------------ ACCEPT
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var socket = server.socket().accept();
                assert socket != null;
                client = socket.getChannel();
            } else {
                client = server.accept();
            }
            assert client != null;
            _Rfc86_Utils.logAccepted(client);
            // ------------------------------------------------------------------------ SEND/RECEIVE
            assert client.isBlocking();
            try (client) {
                final var digest = _Rfc862Utils.newDigest();
                final var buffer = _Rfc86_Utils.newBuffer();
                assert buffer.hasArray();
                final var slice = buffer.slice();
                assert slice.hasArray();
                assert slice.array() == buffer.array();
                var bytes = 0; // number of bytes read so far
                int r; // number of bytes read
                int w; // number of bytes written
                while (true) {
                    // ------------------------------------------------------------------------ read
                    final var hasRemaining = buffer.hasRemaining();
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
                        client.shutdownInput(); // not required
                        break;
                    }
                    assert r > 0 || !hasRemaining;
                    bytes += r;
                    // ----------------------------------------------------------------------- write
                    buffer.flip(); // limit -> position, position -> zero
                    assert buffer.hasRemaining();
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
                    digest.update(slice.position(0).limit(buffer.position()));
                    buffer.compact();
                    assert buffer.position() == 0;
                    assert buffer.limit() == buffer.capacity();
                } // end-of-while
                // ------------------------------------------------------------------ write remained
                assert buffer.position() == 0;
                buffer.flip();
                assert !buffer.hasRemaining();
                while (buffer.hasRemaining()) {
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
                }
                client.shutdownOutput(); // not required
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
            } // try-with-client
        } // try-with-server
    } // main

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
