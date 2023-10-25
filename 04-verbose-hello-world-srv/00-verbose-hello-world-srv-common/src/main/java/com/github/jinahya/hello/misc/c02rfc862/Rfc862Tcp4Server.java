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
                // -------------------------------------------------------------------- receive/send
                try (var attachment = new Rfc862Tcp4ServerAttachment(client)) {
                    // ------------------------------------------------------------------------ read
                    for (int r, w; ; ) {
                        r = attachment.read();
                        assert r >= -1;
                        if (r == -1) {
                            break;
                        }
                        // ------------------------------------------------------------------- write
                        w = attachment.write();
                        assert w >= 0;
                    }
                    // ------------------------------------------------------------ write-to-the-end
                    for (int w; ; ) {
                        w = attachment.write();
                        assert w >= 0;
                        if (w == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
