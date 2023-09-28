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

import com.github.jinahya.hello.misc._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862Tcp2Server {

    public static void main(final String... args) throws Exception {
        try (var server = ServerSocketChannel.open()) {
            server.bind(_Rfc862Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            assert server.isBlocking();
            final SocketChannel client;
            if (ThreadLocalRandom.current().nextBoolean()) {
                final var socket = server.socket().accept();
                client = socket.getChannel();
            } else {
                client = server.accept();
            }
            _Rfc86_Utils.logAccepted(client);
            assert client != null;
            assert client.isBlocking();
            try (client; var attachment = new Rfc862Tcp2ServerAttachment(client)) {
                for (boolean read = true; ; ) {
                    if (read && attachment.read() == -1) {
                        read = false;
                    }
                    if (attachment.write() == 0 && !read) {
                        break;
                    }
                }
            }
        }
    }

    private Rfc862Tcp2Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
