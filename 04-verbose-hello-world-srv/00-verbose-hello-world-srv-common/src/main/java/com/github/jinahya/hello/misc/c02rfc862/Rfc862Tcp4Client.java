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

import com.github.jinahya.hello.util._TcpUtils;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

@Slf4j
class Rfc862Tcp4Client {

    public static void main(final String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var client = AsynchronousSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(_Rfc862Constants.ADDR).get(_Rfc86_Constants.CONNECT_TIMEOUT,
                                                      _Rfc86_Constants.CONNECT_TIMEOUT_UNIT);
            _TcpUtils.logConnected(client);
            // ------------------------------------------------------------------------ send/receive
            try (var attachment = new Rfc862Tcp4ClientAttachment(client)) {
                for (int w, r; ; ) {
                    // ----------------------------------------------------------------------- write
                    w = attachment.write();
                    assert w >= 0;
                    if (w == 0) {
                        break;
                    }
                    // ------------------------------------------------------------------------ read
                    r = attachment.read();
                    assert r >= 0;
                }
                // ----------------------------------------------------------------- shutdown-output
                client.shutdownOutput();
                // ----------------------------------------------------------------- read-to-the-end
                for (int r; ; ) {
                    r = attachment.read();
                    if (r == -1) {
                        break;
                    }
                    assert r >= 0;
                }
            }
        }
    }

    private Rfc862Tcp4Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
