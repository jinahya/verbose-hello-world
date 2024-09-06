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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({"java:S127"})
class Rfc863Tcp2Server extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            assert server.isBlocking(); // !!!
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 1));
            // ------------------------------------------------------------------------------ accept
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                client = server.socket().accept().getChannel();
            } else {
                client = server.accept();
            }
            logAccepted(client);
            assert client.isBlocking(); // !!!
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            int bytes = 0;
            final var buffer = newBuffer();
            // ------------------------------------------------------------------------------- read/
            try (client) {
                for (int r; ; bytes += r) {
                    // ------------------------------------------------------------------------ read
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    assert buffer.hasRemaining();
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        r = client.socket().getInputStream().read(
                                buffer.array(),
                                buffer.arrayOffset() + buffer.position(),
                                buffer.remaining()
                        );
                        if (r == -1) {
                            break;
                        }
                        buffer.position(buffer.position() + r);
                    } else {
                        r = client.read(buffer);
                        if (r == -1) {
                            break;
                        }
                    }
                    assert r > 0; // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, r);
                }
            }
            // --------------------------------------------------------------------------------- log
            logServerBytes(bytes);
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
