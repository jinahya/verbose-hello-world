package com.github.jinahya.hello.miscellaneous.m1_rfc863;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc863TcpServer1 {

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    static final int PORT = 9 + 51000;

    public static void readAndClose(Socket client) throws IOException {
        try (client) {
            log.debug("[S] accepted from {}", client.getRemoteSocketAddress());
            var bytes = 0L;
            for (; true; bytes++) {
                var read = client.getInputStream().read();
                if (read == -1) {
                    break;
                }
            }
            log.debug("[S] byte(s) read: {}", bytes);
        }
    }

    public static void main(String... args) throws IOException {
        try (var server = new ServerSocket()) {
            var endpoint = new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT);
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            server.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            try (var client = server.accept()) {
                log.debug("[S] accepted from {}, through {}", client.getRemoteSocketAddress(),
                          client.getLocalSocketAddress());
                var count = 0L;
                for (; true; count++) {
                    var b = client.getInputStream().read();
                    if (b == -1) {
                        break;
                    }
                }
                log.debug("[S] byte(s) received (and discarded): {}", count);
            }
        }
    }

    private Rfc863TcpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
