package com.github.jinahya.hello.miscellaneous.rfc862;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// https://datatracker.ietf.org/doc/html/rfc862
// https://stackoverflow.com/q/23301598/330457
@Slf4j
class Rfc862Tcp3Server {

    static final InetAddress HOST = Rfc862Tcp2Server.HOST;

    static final int PORT = Rfc862Tcp2Server.PORT;

    static final int CAPACITY = Rfc862Tcp2Server.CAPACITY;

    public static void main(String... args)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(HOST, PORT));
            log.debug("[S] bound to {}", server.getLocalAddress());
            try (var client = server.accept().get(8L, TimeUnit.SECONDS)) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                var buffer = ByteBuffer.allocate(CAPACITY);
                while (true) {
                    var read = client.read(buffer).get(8L, TimeUnit.SECONDS);
                    log.debug("[S] read: {}", read);
                    if (read == -1) {
                        client.shutdownInput();
                        break;
                    }
                    buffer.flip(); // limit -> position; position -> zero
                    var written = client.write(buffer).get();
                    log.debug("[S] written: {}", written);
                    buffer.compact();
                }
                for (buffer.flip(); buffer.hasRemaining(); ) {
                    var written = client.write(buffer).get();
                    log.debug("[S] written: {}", written);
                }
                log.debug("[S] closing client...");
            }
            log.debug("[S] closing server...");
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
