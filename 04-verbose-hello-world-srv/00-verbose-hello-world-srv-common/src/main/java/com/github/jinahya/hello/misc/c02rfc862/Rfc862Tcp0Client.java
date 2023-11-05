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
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp0Client {

    public static void main(final String... args) throws IOException {
        try (var client = new Socket()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // ------------------------------------------------------------------- connect/configure
            client.connect(_Rfc862Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_MILLIS);
            _TcpUtils.logConnected(client);
            client.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            _Rfc862Utils.logClientBytes(bytes);
            // -------------------------------------------------------------------------------- loop
            for (int b; bytes > 0; bytes--) {
                // --------------------------------------------------------------------------- write
                b = ThreadLocalRandom.current().nextInt();
                client.getOutputStream().write(b);
                client.getOutputStream().flush();
                digest.update((byte) b);
                // ---------------------------------------------------------------------------- read
                b = client.getInputStream().read();
                if (b == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            _Rfc862Utils.logDigest(digest);
        }
    }

    private Rfc862Tcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
