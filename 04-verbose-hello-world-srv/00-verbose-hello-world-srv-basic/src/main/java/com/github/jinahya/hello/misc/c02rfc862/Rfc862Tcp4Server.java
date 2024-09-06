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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc862Tcp4Server extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 1));
            // ------------------------------------------------------------------------------ accept
            try (var client = logAccepted(server.accept().get())) {
                // ------------------------------------------------------------------------- prepare
                final var digest = newDigest();
                var bytes = 0L;
                final var buffer = newBuffer();
                // ---------------------------------------------------------------------- read/write
                for (int r, w; ; bytes += r) {
                    // ------------------------------------------------------------------------ read
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    assert buffer.hasRemaining();
                    r = client.read(buffer).get();
                    if (r == -1) {
                        break;
                    }
                    assert r > 0; // why?
                    // ----------------------------------------------------------------------- write
                    assert buffer.position() > 0;
                    buffer.flip();
                    assert buffer.hasRemaining(); // why?
                    w = client.write(buffer).get();
                    assert w > 0; // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                    buffer.compact();
                }
                // ----------------------------------------------------------------- write-remaining
                buffer.flip();
                for (int w; buffer.hasRemaining(); ) {
                    w = client.write(buffer).get();
                    assert w > 0; // why?
                    JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                }
                // ----------------------------------------------------------------------------- log
                logServerBytes(bytes);
                logDigest(digest);
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
