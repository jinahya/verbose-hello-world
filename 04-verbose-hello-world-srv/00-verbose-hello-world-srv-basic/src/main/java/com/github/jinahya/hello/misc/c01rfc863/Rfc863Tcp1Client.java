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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp1Client extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
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
            final var array = newArray();
            var bytes = logClientBytes(newRandomBytes());
            // -------------------------------------------------------------------------------- loop
            while (bytes > 0) {
                // --------------------------------------------------------------------------- write
                ThreadLocalRandom.current().nextBytes(array);
                final var l = Math.min(array.length, bytes);
                client.getOutputStream().write(array, 0, l);
                digest.update(array, 0, l);
                bytes -= l;
            }
            // ------------------------------------------------------------------------------- flush
            client.getOutputStream().flush();
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
