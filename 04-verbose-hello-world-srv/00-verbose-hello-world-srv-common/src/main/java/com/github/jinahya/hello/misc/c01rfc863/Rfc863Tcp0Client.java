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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp0Client {

    public static void main(final String... args) throws Exception {
        try (var client = new Socket()) {
            // -------------------------------------------------------------------------------- BIND
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            // ----------------------------------------------------------------------------- CONNECT
            client.connect(_Rfc863Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_IN_MILLIS);
            _Rfc86_Utils.logConnected(client);
            // ------------------------------------------------------------------------ SEND/RECEIVE
            final var digest = _Rfc863Utils.newDigest();
            var bytes = _Rfc863Utils.logClientBytes(_Rfc86_Utils.randomBytes()); // bytes to send
            int b; // byte to write
            for (; bytes > 0; bytes--) {
                // --------------------------------------------------------------------------- write
                b = ThreadLocalRandom.current().nextInt(256); // [0..256)
                client.getOutputStream().write(b);
                client.getOutputStream().flush(); // TODO: move to out of while loop
                // -------------------------------------------------------------------------- digest
                digest.update((byte) b);
            }
            _Rfc863Utils.logDigest(digest);
        }
    }

    private Rfc863Tcp0Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
