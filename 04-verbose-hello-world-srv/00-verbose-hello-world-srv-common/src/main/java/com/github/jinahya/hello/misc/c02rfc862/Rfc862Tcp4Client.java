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
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

@Slf4j
@SuppressWarnings({
        "java:S127" // loop counter assigned in the loop body
})
class Rfc862Tcp4Client {

    public static void main(final String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        // ------------------------------------------------------------------------------------ open
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(_Rfc862Constants.ADDR)
                    .get(_Rfc86_Constants.CONNECT_TIMEOUT, _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _TcpUtils.logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            assert bytes >= 0;
            _Rfc862Utils.logClientBytes(bytes);
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.hasArray();
            buffer.position(buffer.limit()); // for what?
            assert !buffer.hasRemaining();
            // ------------------------------------------------------------------------ send/receive
            for (int w, r; bytes > 0; bytes -= w) {
                // --------------------------------------------------------------------------- write
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                assert buffer.hasRemaining();
                w = client.write(buffer)
                        .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                assert w > 0; // why?
                JavaSecurityUtils.updateDigest(digest, buffer, w);
                // ---------------------------------------------------------------------------- read
                final var limit = buffer.limit();
                buffer.flip();
                assert buffer.hasRemaining(); // why?
                r = client.read(buffer)
                        .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
                assert r >= -1;
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
                assert r > 0; // why?
                buffer.position(buffer.limit()).limit(limit);
            }
            _Rfc862Utils.logDigest(digest);
            // --------------------------------------------------------------------- shutdown-output
            client.shutdownOutput();
            // --------------------------------------------------------------------- read-to-the-end
            for (int r; ; ) {
                buffer.clear();
                r = client.read(buffer)
                        .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
                assert r >= -1;
                if (r == -1) {
                    break;
                }
            }
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
