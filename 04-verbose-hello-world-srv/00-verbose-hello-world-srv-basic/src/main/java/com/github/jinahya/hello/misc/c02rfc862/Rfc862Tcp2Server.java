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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Server extends _Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            assert server.isBlocking(); // !!!
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 1));
            // -------------------------------------------------------------------- accept/configure
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                client = server.socket().accept().getChannel();
            } else {
                client = server.accept();
            }
            logAccepted(client);
            assert client.isBlocking(); // !!!
            // ------------------------------------------------------------------------ read / write
            try (client) {
                // ------------------------------------------------------------------------- prepare
                final var digest = newDigest();
                var bytes = 0L;
                final var buffer = newBuffer();
                while (true) {
                    // ------------------------------------------------------------------------ read
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        final var r = client.socket().getInputStream().read(
                                buffer.array(),
                                buffer.arrayOffset() + buffer.position(),
                                buffer.remaining()
                        );
                        if (r != -1) {
                            buffer.position(buffer.position() + r);
                            bytes += r;
                        }
                    } else {
                        final var r = client.read(buffer);
                        if (r == -1) {
                            break;
                        }
                        bytes += r;
                    }
                    // ----------------------------------------------------------------------- write
                    buffer.flip(); // limit -> position, position -> zero
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.socket().getOutputStream().write(
                                buffer.array(),
                                buffer.arrayOffset() + buffer.position(),
                                buffer.remaining()
                        );
                        client.socket().getOutputStream().flush();
                        digest.update(buffer);
                    } else {
                        final var w = client.write(buffer);
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    }
                    assert !buffer.hasRemaining(); // why?
                    buffer.compact();
                    assert buffer.position() == 0; // why?
                    assert buffer.limit() == buffer.capacity(); // why?
                }
                // ------------------------------------------------------------------ write-remained
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    client.write(buffer);
                }
                // ----------------------------------------------------------------------------- log
                logServerBytes(bytes);
                logDigest(digest);
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
