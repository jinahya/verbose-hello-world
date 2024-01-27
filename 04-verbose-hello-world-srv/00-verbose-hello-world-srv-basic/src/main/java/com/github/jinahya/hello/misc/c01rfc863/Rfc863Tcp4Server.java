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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc863Tcp4Server extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, 1);
            logBound(server);
            // ------------------------------------------------------------------------------ accept
            try (var client = server.accept().get()) {
                logAccepted(client);
                // ------------------------------------------------------------------------- prepare
                var bytes = 0L;
                final var digest = newDigest();
                // ---------------------------------------------------------------------------- read
                for (var buffer = newBuffer(); ; ) {
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    final var r = client.read(buffer).get();
                    if (r == -1) {
                        break;
                    }
                    bytes += r;
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, r);
                }
                // ----------------------------------------------------------------------------- log
                logServerBytes(bytes);
                logDigest(digest);
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
