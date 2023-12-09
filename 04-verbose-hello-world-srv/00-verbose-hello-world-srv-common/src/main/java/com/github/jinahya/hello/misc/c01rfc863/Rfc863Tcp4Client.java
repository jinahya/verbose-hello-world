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
import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings({
        "java:S127"
})
class Rfc863Tcp4Client {

    public static void main(final String... args)
            throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(_Rfc863Constants.ADDR)
                    .get(_Rfc86_Constants.CONNECT_TIMEOUT, _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _TcpUtils.logConnected(client);
            // ----------------------------------------------------------------------------- prepare
            var bytes = _Rfc863Utils.logClientBytes(_Rfc86_Utils.newRandomBytes());
            final var digest = _Rfc863Utils.newDigest();
            final var buffer = _Rfc86_Utils.newBuffer();
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.limit(Math.min(buffer.limit(), bytes));
            // ------------------------------------------------------------------------------- write
            for (int w; bytes > 0; bytes -= w) {
                if (!buffer.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.clear().limit(Math.min(buffer.limit(), bytes));
                }
                assert buffer.hasRemaining();
                w = client.write(buffer)
                        .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
                JavaSecurityUtils.updateDigest(digest, buffer, w);
            }
            // -------------------------------------------------------------------------------------
            _Rfc863Utils.logDigest(digest);
        }
    }

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
