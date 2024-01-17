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
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.StandardSocketOptions;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc863Tcp1Server {

    public static void main(final String... args) throws Exception {
        try (var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                server.setReuseAddress(true);
            } else {
                server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            }
            server.bind(_Rfc863Constants.ADDR, 1);
            _TcpUtils.logBound(server);
            // ------------------------------------------------------------------------------ accept
            server.setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_MILLIS);
            try (var client = server.accept()) {
                _TcpUtils.logAccepted(client);
                client.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
                // ------------------------------------------------------------------------- prepare
                final var digest = _Rfc863Utils.newDigest();
                var bytes = 0L;
                final var array = _Rfc86_Utils.newArray();
                // ---------------------------------------------------------------------------- read
                for (int r; ; bytes += r) {
                    r = client.getInputStream().read(array);
                    if (r == -1) {
                        break;
                    }
                    digest.update(array, 0, r);
                }
                _Rfc863Utils.logServerBytes(bytes);
                _Rfc863Utils.logDigest(digest);
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
