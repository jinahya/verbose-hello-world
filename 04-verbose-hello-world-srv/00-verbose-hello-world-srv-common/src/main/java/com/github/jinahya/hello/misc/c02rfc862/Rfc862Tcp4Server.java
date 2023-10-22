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
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousServerSocketChannel;

@Slf4j
class Rfc862Tcp4Server {

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            // ------------------------------------------------------------------------------ ACCEPT
            try (var client = server.accept().get(_Rfc86_Constants.ACCEPT_TIMEOUT,
                                                  _Rfc86_Constants.ACCEPT_TIMEOUT_UNIT)) {
                _Rfc86_Utils.logAccepted(client);
                // -------------------------------------------------------------------- RECEIVE/SEND
                try (var attachment = new Rfc862Tcp4ServerAttachment(client)) {
                    // ------------------------------------------------------------------------ read
                    for (int r; (r = attachment.read()) != -1; ) {
                        assert r >= 0;
                        // ------------------------------------------------------------------- write
                        final var w = attachment.write();
                        assert w >= 0;
                    }
                    while (attachment.write() > 0) {
                        // do nothing
                    }
                }
            }
        }
    }

    private Rfc862Tcp4Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
