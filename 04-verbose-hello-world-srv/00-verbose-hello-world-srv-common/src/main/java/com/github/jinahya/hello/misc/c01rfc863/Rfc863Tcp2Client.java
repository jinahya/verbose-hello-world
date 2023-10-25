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

import com.github.jinahya.hello.util._TcpUtils;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc863Tcp2Client {

    public static void main(final String... args) throws IOException {
        try (var client = SocketChannel.open()) {
            assert client.isBlocking();
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            // ----------------------------------------------------------------------------- connect
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().connect(
                        _Rfc863Constants.ADDR,                        // <endpoint>
                        (int) _Rfc86_Constants.CONNECT_TIMEOUT_MILLIS // <timeout>
                );
                _TcpUtils.logConnected(client.socket());
            } else {
                final var connected = client.connect(_Rfc863Constants.ADDR);
                assert connected || !client.isBlocking();
                _TcpUtils.logConnected(client.socket());
            }
            assert client.isConnected();
            assert client.socket().isConnected();
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc863Utils.newDigest();
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.hasArray();
            var bytes = _Rfc863Utils.logClientBytes(_Rfc86_Utils.randomBytes());
            // ------------------------------------------------------------------------------- write
            for (int w; bytes > 0; bytes -= w) {
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                assert buffer.hasRemaining();
                if (ThreadLocalRandom.current().nextBoolean()) {
                    w = buffer.remaining();
                    client.socket().getOutputStream().write(
                            buffer.array(),                           // <b>
                            buffer.arrayOffset() + buffer.position(), // <off>
                            buffer.remaining()                        // <len>
                    );
                    client.socket().getOutputStream().flush();
                    buffer.position(buffer.limit());
                } else {
                    w = client.write(buffer);
                }
                assert w > 0; // why?
                assert !buffer.hasRemaining(); // why?
                JavaSecurityUtils.updateDigest(digest, buffer, w);
            }
            _Rfc863Utils.logDigest(digest);
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
