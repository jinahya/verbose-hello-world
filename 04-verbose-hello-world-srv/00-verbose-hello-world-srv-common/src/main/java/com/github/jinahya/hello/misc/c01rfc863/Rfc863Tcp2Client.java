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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Rfc863Tcp3Server
 */
@Slf4j
class Rfc863Tcp2Client {

    public static void main(final String... args) throws Exception {
        try (var client = SocketChannel.open()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalAddress());
            }
            assert client.isBlocking();
            // ----------------------------------------------------------------------------- CONNECT
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().connect(
                        _Rfc863Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_IN_MILLIS
                );
                _Rfc86_Utils.logConnected(client.socket());
            } else {
                if (!client.connect(_Rfc863Constants.ADDR)) {
                    log.error("failed to connect");
                    return;
                }
                _Rfc86_Utils.logConnected(client);
            }
            // -------------------------------------------------------------------------------- SEND
            try (var attachment = new Rfc863Tcp2ClientAttachment(client)) {
                while (attachment.write() > 0) {
                    // does nothing
                }
            }
        }
    }

    private Rfc863Tcp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
