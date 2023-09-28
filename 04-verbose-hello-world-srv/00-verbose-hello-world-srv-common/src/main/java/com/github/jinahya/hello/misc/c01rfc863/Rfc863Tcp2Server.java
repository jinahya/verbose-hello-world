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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalAddress());
            assert server.isBlocking();
            // ------------------------------------------------------------------------------ ACCEPT
            final SocketChannel client;
            server.socket().setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS);
            if (ThreadLocalRandom.current().nextBoolean()) {
                client = server.socket().accept().getChannel();
            } else {
                client = server.accept();
            }
            assert client != null;
            // ----------------------------------------------------------------------------- RECEIVE
            client.socket().setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_IN_MILLIS);
            final var digest = _Rfc863Utils.newDigest();
            int bytes = 0;
            final var buffer = _Rfc86_Utils.newBuffer();
            for (int r; ; ) {
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    r = client.socket().getInputStream().read(
                            buffer.array(),
                            buffer.arrayOffset() + buffer.position(),
                            buffer.remaining()
                    );
                } else {
                    r = client.read(buffer);
                }
                if (r == -1) {
                    break;
                }
                bytes += r;
            }
            _Rfc863Utils.logServerBytes(bytes);
            _Rfc863Utils.logDigest(digest);
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
