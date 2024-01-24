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
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp1Client {

    public static void main(final String... args) throws Exception {
        try (var client = new Socket()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(_Rfc862Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_MILLIS);
            _TcpUtils.logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            _Rfc862Utils.logClientBytes(bytes);
            final var array = _Rfc86_Utils.newArray();
            assert array.length > 0;
            // -------------------------------------------------------------------------- write/read
            while (bytes > 0) {
                // --------------------------------------------------------------------------- write
                ThreadLocalRandom.current().nextBytes(array);
                final int l = Math.min(array.length, bytes);
                client.getOutputStream().write(array, 0, l);
                bytes -= l;
                digest.update(array, 0, l);
                // ---------------------------------------------------------------------------- read
                if (client.getInputStream().read(array, 0, l) == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            // ---------------------------------------------------------------------- flush/shutdown
            log.debug("flushing client.outputstream...");
            client.getOutputStream().flush();
            log.debug("shutting down client.output...");
            client.shutdownOutput();
            // --------------------------------------------------------------------- read-to-the-end
            log.debug("[client] reading to the end...");
            while (client.getInputStream().read(array) != -1) {
                // empty
            }
            // --------------------------------------------------------------------------------- log
            _Rfc862Utils.logDigest(digest);
            log.debug("[client] closing the client...");
        }
        log.debug("[client] end-of-main");
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
