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
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc862Tcp4Server {

    public static void main(final String... args) throws Exception {
        // ------------------------------------------------------------------------------------ open
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------------------ accept
            try (var client = server.accept().get(_Rfc86_Constants.ACCEPT_TIMEOUT,
                                                  _Rfc86_Constants.ACCEPT_TIMEOUT_UNIT)) {
                _TcpUtils.logAccepted(client);
                // ------------------------------------------------------------------------- prepare
                final var digest = _Rfc862Utils.newDigest();
                var bytes = 0L;
                final var buffer = _Rfc86_Utils.newBuffer();
                // ---------------------------------------------------------------------- read/write
                while (true) {
                    // ------------------------------------------------------------------------ read
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    final int r = client.read(buffer).get(_Rfc86_Constants.READ_TIMEOUT,
                                                          _Rfc86_Constants.READ_TIMEOUT_UNIT);
                    if (r == -1) {
                        break;
                    }
                    bytes += r;
                    // ----------------------------------------------------------------------- write
                    buffer.flip();
                    final int w = client.write(buffer).get(_Rfc86_Constants.WRITE_TIMEOUT,
                                                           _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    buffer.compact();
                }
                // ----------------------------------------------------------------- write-remaining
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    final int w = client.write(buffer).get(_Rfc86_Constants.WRITE_TIMEOUT,
                                                           _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                }
                client.shutdownOutput();
                // ----------------------------------------------------------------------------- log
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
                log.debug("[server] closing client...");
            }
            log.debug("[server] closing server...");
        }
        log.debug("[server] end-of-main");
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
