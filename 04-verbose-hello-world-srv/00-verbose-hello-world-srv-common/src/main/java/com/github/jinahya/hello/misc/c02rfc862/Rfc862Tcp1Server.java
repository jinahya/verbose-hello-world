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

import java.net.ServerSocket;

@Slf4j
class Rfc862Tcp1Server {

    public static void main(final String... args) throws Exception {
        try (var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // -------------------------------------------------------------------- accept/configure
            server.setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS);
            try (var client = server.accept()) {
                _TcpUtils.logAccepted(client);
//                client.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
                // ------------------------------------------------------------------------- prepare
                final var digest = _Rfc862Utils.newDigest();
                var bytes = 0L;
                final var array = _Rfc86_Utils.newArray();
                assert array.length > 0;
                // ---------------------------------------------------------------------- read/write
                while (true) {
                    // ------------------------------------------------------------------------ read
                    final int r = client.getInputStream().read(array);
                    if (r == -1) {
                        break;
                    }
                    bytes += r;
                    digest.update(array, 0, r);
                    // ----------------------------------------------------------------------- write
                    client.getOutputStream().write(array, 0, r);
                }
                client.getOutputStream().flush();
                client.shutdownOutput();
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
                log.debug("[server] closing the client...");
            }
            log.debug("[server] closing the server...");
        }
        log.debug("[server] end-of-main");
    }

    private Rfc862Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
