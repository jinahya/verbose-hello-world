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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp0Client extends Rfc862Tcp {

    public static void main(final String... args) throws IOException {
        try (var client = new Socket()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(ADDR);
            logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = logClientBytes(newRandomBytes());
            // ------------------------------------------------------------------------ write / read
            for (int b; bytes > 0; bytes--) {
                // --------------------------------------------------------------------------- write
                b = ThreadLocalRandom.current().nextInt();
                client.getOutputStream().write(b);
                client.getOutputStream().flush();
                digest.update((byte) b);
                // ---------------------------------------------------------------------------- read
                if (client.getInputStream().read() == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
