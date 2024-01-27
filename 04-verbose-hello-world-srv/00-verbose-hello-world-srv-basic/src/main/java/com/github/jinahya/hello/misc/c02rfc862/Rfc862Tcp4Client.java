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

import com.github.jinahya.hello.util.HelloWorldNetUtils;
import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@SuppressWarnings({
        "java:S127" // loop counter assigned in the loop body
})
class Rfc862Tcp4Client extends Rfc862Tcp {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            HelloWorldNetUtils.printSocketOptions(AsynchronousSocketChannel.class, client);
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(ADDR).get();
            logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            final var rdigest = newDigest();
            final var buffer = newBuffer().limit(0);
            var bytes = logClientBytes(newRandomBytes());
            final var copy = bytes;
            var received = 0;
            // -------------------------------------------------------------------------- write/read
//            for (var bytes = logClientBytes(newRandomBytes()); bytes > 0; ) {
            for (; bytes > 0; ) {
                // --------------------------------------------------------------------------- write
                if (!buffer.hasRemaining()) {
                    JavaNioByteBufferUtils.randomize(
                            buffer.clear().limit(Math.min(buffer.remaining(), bytes))
                    );
                }
                assert buffer.hasRemaining();
                final int w = client.write(buffer).get();
                log.debug("client.w: {}", w);
                assert w > 0; // why?
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                bytes -= w;
                // ---------------------------------------------------------------------------- read
                final var limit = buffer.limit();
                buffer.flip(); // limit -> position, position -> zero
                final int r = client.read(buffer).get();
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
                assert r > 0; // why?
                log.debug("client.r: {}, total: {}", r, (received += r));
                buffer.position(buffer.limit()).limit(limit);
                JavaSecurityMessageDigestUtils.updateDigest(rdigest, buffer, r);
            }
            // --------------------------------------------------------------------- shutdown-output
            log.debug("[client] shutting down output...");
            client.shutdownOutput();
            // ---------------------------------------------------------------------- read-remaining
            log.debug("[client] reading remaining...");
            try {
//                for (int r; ;(r = client.read(buffer.clear()).get(10L, TimeUnit.SECONDS)) != -1; ) {
                for (int r; ; ) {
                    r = client.read(buffer.clear()).get(10L, TimeUnit.SECONDS);
                    if (r == -1) {
                        break;
                    }
                    log.debug("[client] remaining client.r: {}, total: {}", r, (received += r));
                    JavaSecurityMessageDigestUtils.updateDigest(rdigest, buffer, r);
                }
            } catch (final TimeoutException te) { // macOS/Monterey
                log.error("[client] time's up while reading remaining", te);
            }
            log.debug("[client] received: {}, copy: {}, {}", received, copy, copy - received);
            logDigest(rdigest);
            assert received == copy;
            // --------------------------------------------------------------------------------- log
            logDigest(digest);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
