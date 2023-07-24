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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
class Rfc862Tcp3Server {

    static final int PORT = 53007; // 7 + 53000

    public static void readWriteAndClose(SocketChannel client) throws IOException {
        try (client) {
            long bytes = 0L;
            var buffer = ByteBuffer.allocate(2);
            while (true) {
                var read = client.read(buffer);
                if (read == -1) {
                    break;
                }
                bytes += read;
                buffer.flip(); // limit -> position, position -> zero
                client.write(buffer);
                buffer.compact(); // move remaining bytes to the front
            } // end-of-while
            for (buffer.flip(); buffer.hasRemaining(); ) {
                client.write(buffer);
            }
            log.debug("[S] {} bytes read/written to {}", bytes, client.getRemoteAddress());
        }
    }

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = ServerSocketChannel.open()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalAddress());
            var executor = Executors.newCachedThreadPool();
            while (server.isOpen()) {
                var client = server.accept();
                executor.submit(() -> {
                    log.debug("[S] connected from {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                    readWriteAndClose(client);
                    return null;
                });
            }
            executor.shutdown();
            var timeout = 4L;
            var unit = TimeUnit.SECONDS;
            if (!executor.awaitTermination(timeout, unit)) {
                log.error("executor not terminated in {} {}", timeout, unit);
            }
        }
    }

    private Rfc862Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
