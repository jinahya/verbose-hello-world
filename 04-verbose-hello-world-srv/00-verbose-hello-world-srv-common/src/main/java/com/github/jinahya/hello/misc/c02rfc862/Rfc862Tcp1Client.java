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

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp1Client {

    public static void main(final String... args) throws Exception {
        try (var client = new Socket()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(_Rfc862Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_MILLIS);
            _TcpUtils.logConnected(client);
            client.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_MILLIS);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = _Rfc86_Utils.newRandomBytes();
            _Rfc862Utils.logClientBytes(bytes);
            final var array = _Rfc86_Utils.newArray();
            // -------------------------------------------------------------------------------- loop
            for (int l, r; bytes > 0; bytes -= l) {
                // --------------------------------------------------------------------------- write
                ThreadLocalRandom.current().nextBytes(array);
                l = Math.min(array.length, bytes);
                client.getOutputStream().write(
                        array, // <b>
                        0,     // <off>
                        l      // <len>
                );
                client.getOutputStream().flush();
                digest.update(array, 0, l);
                // ---------------------------------------------------------------------------- read
                r = client.getInputStream().read(
                        array, // <b>
                        0,     // <off>
                        l      // <len>
                );
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            // --------------------------------------------------------------------- shutdown-output
            client.shutdownOutput();
            // --------------------------------------------------------------------- read-to-the-end
            for (int r; ; ) {
                r = client.getInputStream().read(
                        array // <b>
                );
                if (r == -1) {
                    break;
                }
            }
            _Rfc862Utils.logDigest(digest);
        }
    }

    private Rfc862Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
