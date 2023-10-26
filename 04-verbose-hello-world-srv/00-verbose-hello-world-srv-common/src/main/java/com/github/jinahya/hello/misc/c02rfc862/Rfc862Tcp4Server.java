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

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
class Rfc862Tcp4Server {

    public static void main(final String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
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
                assert buffer.capacity() > 0;
                // -------------------------------------------------------------------- receive/send
                for (int r, w; ; bytes += r) {
                    // ------------------------------------------------------------------------ read
                    if (!buffer.hasRemaining()) {
                        buffer.clear();
                    }
                    assert buffer.hasRemaining();
                    r = client.read(buffer)
                            .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
                    assert r >= -1;
                    if (r == -1) {
                        break;
                    }
                    assert r > 0; // why?
                    // ----------------------------------------------------------------------- write
                    buffer.flip();
                    assert buffer.hasRemaining(); // why?
                    w = client.write(buffer)
                            .get(_Rfc86_Constants.WRITE_TIMEOUT,
                                 _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                    assert w > 0; // why?
                    JavaSecurityUtils.updateDigest(digest, buffer, w);
                    buffer.compact();
                }
                // ---------------------------------------------------------------- write-to-the-end
                buffer.flip();
                for (int w; buffer.hasRemaining(); ) {
                    w = client.write(buffer)
                            .get(_Rfc86_Constants.WRITE_TIMEOUT,
                                 _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                    assert w > 0; // why?
                    JavaSecurityUtils.updateDigest(digest, buffer, w);
                }
                // ------------------------------------------------------------------------- logging
                _Rfc862Utils.logServerBytes(bytes);
                _Rfc862Utils.logDigest(digest);
            }
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
