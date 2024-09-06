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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127" // loop counter assigned in the loop body
})
class Rfc862Tcp4Client extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(ADDR).get();
            logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var buffer = newBuffer().limit(0);
            var bytes = logClientBytes(newRandomBytes());
            // -------------------------------------------------------------------------- write/read
            for (int w, r; bytes > 0; bytes -= w) {
                // --------------------------------------------------------------------------- write
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.remaining(), bytes));
                }
                assert buffer.hasRemaining();
                w = client.write(buffer).get();
                assert w > 0; // why?
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                // ---------------------------------------------------------------------------- read
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    r = client.read(buffer).get();
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    assert r > 0; // why?
                }
                buffer.limit(Math.min(buffer.capacity(), bytes));
            }
            // --------------------------------------------------------------------- shutdown-output
            client.shutdownOutput();
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
