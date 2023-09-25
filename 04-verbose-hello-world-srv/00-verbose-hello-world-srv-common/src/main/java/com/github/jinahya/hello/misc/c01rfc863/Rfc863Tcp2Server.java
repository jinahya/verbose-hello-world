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

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            server.bind(_Rfc863Constants.ADDR, 1);
            log.info("bound to {}", server.getLocalAddress());
            assert server.isBlocking();
            // ------------------------------------------------------------------------------ ACCEPT
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var serverSocket = server.socket();
                serverSocket.setSoTimeout((int) _Rfc86_Constants.ACCEPT_TIMEOUT_IN_MILLIS);
                final var clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout((int) _Rfc86_Constants.READ_TIMEOUT_IN_MILLIS);
                client = clientSocket.getChannel();
                assert client != null;
            } else {
                client = server.accept();
            }
            _Rfc86_Utils.logAccepted(client);
            // ----------------------------------------------------------------------------- RECEIVE
            try (var attachment = new Rfc863Tcp2ServerAttachment(client)) {
                while (attachment.read() != -1) {
                    // does nothing
                }
            }
        }
    }

    private Rfc863Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
