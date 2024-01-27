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

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc863Tcp2Client extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var client = SocketChannel.open()) {
            assert client.isBlocking(); // !!!
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().connect(ADDR);
                logConnected(client.socket());
            } else {
                final var connected = client.connect(ADDR);
                assert connected || !client.isBlocking();
                logConnected(client);
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = logClientBytes(newRandomClientBytes());
            // ------------------------------------------------------------------------------- write
            final var buffer = newBuffer();
            for (int w; bytes > 0; bytes -= w) {
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    w = buffer.remaining();
                    client.socket().getOutputStream().write(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            w
                    );
                    client.socket().getOutputStream().flush();
                    buffer.position(buffer.position() + w);
                } else {
                    w = client.write(buffer);
                }
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
            }
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
