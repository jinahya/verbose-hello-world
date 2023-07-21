package com.github.jinahya.hello.miscellaneous.m1_rfc862;

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

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
public class Rfc862TcpServer1 {

    static final int PORT = 51007; // 7 + 51000

    public static void readWriteAndClose(Socket client) throws IOException {
        try (client) {
            log.debug("[S] accepted from {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            var bytes = 0L;
            for (; true; bytes++) {
                var b = client.getInputStream().read();
                if (b == -1) {
                    break;
                }
                client.getOutputStream().write(b);
                client.getOutputStream().flush();
            }
            log.debug("[S] {} byte(s) read/written", bytes);
        }
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                readWriteAndClose(server.accept());
            }
        }
    }

    private Rfc862TcpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
