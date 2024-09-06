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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc862Tcp2Client extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var client = SocketChannel.open()) {
            assert client.isBlocking(); // !!!
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().connect(ADDR);
            } else {
                final var connected = client.connect(ADDR);
                assert connected || !client.isBlocking();
            }
            assert client.isConnected();
            logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = newBuffer().limit(0);
            var bytes = logClientBytes(newRandomBytes());
            // ------------------------------------------------------------------------ write / read
            for (int w, r; bytes > 0; ) {
                // --------------------------------------------------------------------------- write
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                assert buffer.hasRemaining();
                if (ThreadLocalRandom.current().nextBoolean()) {
                    client.socket().getOutputStream().write(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.remaining()
                    );
                    client.socket().getOutputStream().flush();
                    digest.update(buffer.array(), buffer.arrayOffset() + buffer.position(),
                                  buffer.remaining());
                    bytes -= buffer.remaining();
                    buffer.position(buffer.limit());
                } else {
                    w = client.write(buffer);
                    assert w > 0; // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    bytes -= w;
                }
                assert !buffer.hasRemaining(); // why?
                // ---------------------------------------------------------------------------- read
                buffer.flip(); // limit -> position, position -> zero
                assert buffer.hasRemaining();
                if (ThreadLocalRandom.current().nextBoolean()) {
                    r = client.socket().getInputStream().read(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.remaining()
                    );
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    buffer.position(buffer.position() + r);
                } else {
                    r = client.read(buffer);
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                }
            }
            // --------------------------------------------------------------------- shutdown-output
            client.shutdownOutput();
            // ---------------------------------------------------------------------- read-remaining
            for (int r; ; ) {
                buffer.clear();
                if (ThreadLocalRandom.current().nextBoolean()) {
                    r = client.socket().getInputStream().read(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.remaining()
                    );
                    if (r == -1) {
                        break;
                    }
                } else {
                    r = client.read(buffer);
                    if (r == -1) {
                        break;
                    }
                }
            }
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
