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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc863Tcp4Server {

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.bind(_Rfc863Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------------------ accept
            try (var client = server.accept().get(_Rfc86_Constants.ACCEPT_TIMEOUT,
                                                  _Rfc86_Constants.ACCEPT_TIMEOUT_UNIT)) {
                _TcpUtils.logAccepted(client);
                // ------------------------------------------------------------------------- prepare
                final var digest = _Rfc863Utils.newDigest();
                var bytes = 0L;
                final var buffer = _Rfc86_Utils.newBuffer();
                // ---------------------------------------------------------------------------- read
                for (int r; ; bytes += r) {
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    r = client.read(buffer)
                            .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
                    if (r == -1) {
                        break;
                    }
                    JavaSecurityUtils.updateDigest(digest, buffer, r);
                }
                _Rfc863Utils.logServerBytes(bytes);
                _Rfc863Utils.logDigest(digest);
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
